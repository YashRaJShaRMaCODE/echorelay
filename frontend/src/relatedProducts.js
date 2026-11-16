export function safeLeadHubOrigin(baseUrl) {
  try {
    const url = new URL(baseUrl || "");
    if (url.protocol !== "https:" || url.username || url.password || !["", "/"].includes(url.pathname) || url.search || url.hash) return "";
    return url.origin;
  } catch {
    return "";
  }
}

export async function requestRelatedProducts(baseUrl, project, consent, fetcher = fetch) {
  const origin = safeLeadHubOrigin(baseUrl);
  if (!consent || !origin) return [];
  const response = await fetcher(`${origin}/api/recommendations?project=${encodeURIComponent(project)}&consent=true`);
  if (!response.ok) throw new Error("Suggestions unavailable");
  const result = await response.json();
  return (Array.isArray(result.items) ? result.items : []).filter((item) => {
    if (typeof item?.name !== "string" || typeof item?.reason !== "string" || typeof item?.url !== "string") return false;
    try {
      const url = new URL(item.url);
      return url.protocol === "https:" && !url.username && !url.password && url.hostname !== "example.com" && !url.hostname.endsWith(".example.com");
    } catch {
      return false;
    }
  });
}
