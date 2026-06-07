import { setTokens } from "@/lib/auth/tokenStorage";
import { roleHome, ROLES } from "@/lib/auth/roles";

/**
 * DEV-ONLY preview login.
 *
 * Mints an unsigned, client-side-only JWT so the app treats you as logged in
 * without a running backend. jwt-decode only reads the payload (it doesn't
 * verify the signature), so this is enough to pass the route guards and render
 * each role's UI. API calls will still fail while the backend is down — pages
 * just show their empty/error states.
 *
 * Guarded by import.meta.env.DEV at the call sites, so it never ships in a
 * production build.
 */

// Stable fake UUIDs per role (used as userId / pharmacyId in API paths).
const FAKE_IDS = {
  [ROLES.CUSTOMER]: "00000000-0000-0000-0000-0000000c0001",
  [ROLES.PHARMACY]: "00000000-0000-0000-0000-0000000b0001",
  [ROLES.ADMIN]: "00000000-0000-0000-0000-0000000a0001",
};

function base64url(obj) {
  return btoa(JSON.stringify(obj))
    .replace(/\+/g, "-")
    .replace(/\//g, "_")
    .replace(/=+$/, "");
}

function makeFakeToken(role, userId, email) {
  const header = base64url({ alg: "none", typ: "JWT" });
  const payload = base64url({
    sub: userId,
    email,
    role,
    exp: Math.floor(Date.now() / 1000) + 60 * 60 * 24, // 24h
  });
  return `${header}.${payload}.dev`;
}

/** Store a fake session for the given role and navigate to its home. */
export function devLoginAs(role) {
  const userId = FAKE_IDS[role];
  const email = `${role.toLowerCase()}@preview.dev`;
  setTokens({ accessToken: makeFakeToken(role, userId, email), refreshToken: "dev-refresh" });
  window.location.assign(roleHome(role));
}
