import { useState } from "react";
import { requestRelatedProducts, safeLeadHubOrigin } from "../relatedProducts.js";

const leadHubOrigin = safeLeadHubOrigin(import.meta.env.VITE_LEADHUB_URL);

export function LeadHubSignup() {
  const [status, setStatus] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [recommendations, setRecommendations] = useState([]);
  const [recommendationStatus, setRecommendationStatus] = useState("");

  if (!leadHubOrigin) return null;

  async function handleSubmit(event) {
    event.preventDefault();
    const form = event.currentTarget;
    const fields = new FormData(form);
    setSubmitting(true);
    setStatus("");
    try {
      const response = await fetch(`${leadHubOrigin}/api/subscribe`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          project: "echorelay",
          email: fields.get("email"),
          consent: fields.get("consent") === "on",
          website: fields.get("website"),
          source: window.location.hostname,
        }),
      });
      const result = await response.json();
      if (!response.ok || !result.ok) throw new Error(result.error || "Signup failed");
      form.reset();
      setStatus(result.message || "Check your inbox to confirm your signup.");
    } catch {
      setStatus("Signup is unavailable right now. Please try again later.");
    } finally {
      setSubmitting(false);
    }
  }

  async function handleRecommendations(event) {
    const checkbox = event.currentTarget;
    setRecommendations([]);
    setRecommendationStatus(checkbox.checked ? "Loading suggestions…" : "");
    if (!checkbox.checked) return;
    try {
      const items = await requestRelatedProducts(leadHubOrigin, "echorelay", true);
      if (checkbox.checked) {
        setRecommendations(items);
        setRecommendationStatus(items.length ? "" : "No related products are available right now.");
      }
    } catch {
      if (checkbox.checked) setRecommendationStatus("Suggestions are unavailable right now.");
    }
  }

  return (
    <section className="lead-signup" aria-labelledby="lead-signup-title">
      <p className="lead-signup-kicker">Signal Room / Field Notes</p>
      <h2 id="lead-signup-title">Make your next audio bug easier to catch.</h2>
      <p>Occasional diagnostic ideas and pilot updates. No microphone audio or device details are collected by this signup.</p>
      <form onSubmit={handleSubmit}>
        <label className="sr-only" htmlFor="lead-signup-email">Email address</label>
        <input id="lead-signup-email" name="email" type="email" autoComplete="email" maxLength={254} placeholder="you@example.com" required />
        <label className="lead-signup-consent"><input name="consent" type="checkbox" required />Email me Signal Room updates. I can unsubscribe anytime.</label>
        <label className="lead-signup-consent"><input type="checkbox" onChange={handleRecommendations} />Show me relevant products too. No microphone or device information is shared.</label>
        <div className="lead-signup-honeypot" aria-hidden="true"><label htmlFor="lead-signup-website">Leave empty</label><input id="lead-signup-website" name="website" tabIndex={-1} autoComplete="off" /></div>
        <button type="submit" disabled={submitting}>{submitting ? "Joining…" : "Get updates"}<span aria-hidden="true">↗</span></button>
        <p className="lead-signup-status" role="status" aria-live="polite">{status}</p>
      </form>
      <div className="lead-signup-recommendations" aria-live="polite">
        {recommendationStatus && <p>{recommendationStatus}</p>}
        {recommendations.length > 0 && <><h3>Our other products</h3><ul>{recommendations.map((item) => <li key={item.id}><a href={item.url} target="_blank" rel="noopener noreferrer">{item.name}</a><span> — {item.reason}</span></li>)}</ul></>}
      </div>
    </section>
  );
}
