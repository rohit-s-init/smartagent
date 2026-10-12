const dateTimeFormat = new Intl.DateTimeFormat("en-IN", {
  day: "numeric",
  month: "short",
  year: "numeric",
  hour: "numeric",
  minute: "2-digit",
});

const dateFormat = new Intl.DateTimeFormat("en-IN", { day: "numeric", month: "short", year: "numeric" });

// "2026-10-12T14:53:07" -> "12 Oct 2026, 2:53 pm"
export function formatDateTime(value) {
  if (!value) return "—";
  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? value : dateTimeFormat.format(date);
}

export function formatDate(value) {
  if (!value) return "—";
  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? value : dateFormat.format(date);
}

// "2026-10-12T14:53:07" -> "3h ago"
export function timeAgo(value) {
  if (!value) return "";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "";

  const seconds = Math.round((Date.now() - date.getTime()) / 1000);
  if (seconds < 45) return "just now";
  const units = [
    ["year", 31536000],
    ["month", 2592000],
    ["week", 604800],
    ["day", 86400],
    ["hour", 3600],
    ["minute", 60],
  ];
  for (const [unit, size] of units) {
    const amount = Math.floor(Math.abs(seconds) / size);
    if (amount >= 1) {
      const label = `${amount} ${unit}${amount > 1 ? "s" : ""}`;
      return seconds >= 0 ? `${label} ago` : `in ${label}`;
    }
  }
  return "just now";
}

// "14:30:00" -> "2:30 PM"
export function formatTime(value) {
  if (!value) return "—";
  const [hours, minutes] = value.split(":").map(Number);
  if (Number.isNaN(hours)) return value;
  const suffix = hours >= 12 ? "PM" : "AM";
  const hour12 = hours % 12 === 0 ? 12 : hours % 12;
  return `${hour12}:${String(minutes ?? 0).padStart(2, "0")} ${suffix}`;
}

export function formatScore(score, scoreType) {
  if (score === null || score === undefined) return "—";
  const type = (scoreType || "").toLowerCase();
  const value = Number.isInteger(score) ? score : Number(score).toFixed(2).replace(/\.?0+$/, "");
  if (type.includes("percent") && !type.includes("percentile")) return `${value}%`;
  return scoreType ? `${value} ${scoreType}` : String(value);
}

export function formatNumber(value) {
  return new Intl.NumberFormat("en-IN").format(value ?? 0);
}

export function initials(name, fallback = "?") {
  const source = (name || "").trim();
  if (!source) return fallback;
  const parts = source.split(/\s+/);
  return (parts[0][0] + (parts.length > 1 ? parts[parts.length - 1][0] : "")).toUpperCase();
}

export function displayName(lead) {
  return lead?.name?.trim() || "Unnamed lead";
}

// students on the call vs the voice agent
export function isStudentRole(role) {
  return /user|student|human|lead|caller/i.test(role || "");
}

export function titleCase(text) {
  return (text || "")
    .toLowerCase()
    .replace(/(^|\s|_)\w/g, (match) => match.toUpperCase())
    .replace(/_/g, " ");
}

// stable colour per lead so avatars are recognisable across pages
const AVATAR_HUES = [250, 200, 160, 30, 340, 280, 190, 10];
export function avatarHue(seed) {
  const text = String(seed ?? "");
  let hash = 0;
  for (let i = 0; i < text.length; i++) hash = (hash * 31 + text.charCodeAt(i)) >>> 0;
  return AVATAR_HUES[hash % AVATAR_HUES.length];
}
