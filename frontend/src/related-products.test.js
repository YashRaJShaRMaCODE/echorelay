import { test } from "node:test";
import assert from "node:assert/strict";
import { requestRelatedProducts, safeLeadHubOrigin } from "./relatedProducts.js";

test("related suggestions require consent and accept only HTTPS product links", async () => {
  assert.equal(safeLeadHubOrigin("https://leads.example.test/"), "https://leads.example.test");
  assert.equal(safeLeadHubOrigin("http://leads.example.test"), "");
  let requested = "";
  const fetcher = async (url) => {
    requested = url;
    return { ok: true, json: async () => ({ items: [
      { id: "portfolio", name: "YashRaj Studio", url: "https://studio.example.test/", reason: "Build support" },
      { id: "unsafe", name: "Unsafe", url: "javascript:alert(1)", reason: "Bad" },
    ] }) };
  };
  assert.deepEqual(await requestRelatedProducts("https://leads.example.test", "echorelay", false, fetcher), []);
  assert.equal(requested, "");
  const items = await requestRelatedProducts("https://leads.example.test", "echorelay", true, fetcher);
  assert.equal(requested, "https://leads.example.test/api/recommendations?project=echorelay&consent=true");
  assert.deepEqual(items.map(({ id }) => id), ["portfolio"]);
});
