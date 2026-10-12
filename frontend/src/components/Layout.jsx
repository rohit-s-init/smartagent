import { useEffect, useState } from "react";
import { NavLink, Outlet, useLocation, useNavigate } from "react-router-dom";
import {
  AudioLines,
  BookOpen,
  CalendarClock,
  ClipboardList,
  GraduationCap,
  LayoutDashboard,
  Menu,
  MessageSquareText,
  MessagesSquare,
  Moon,
  NotebookPen,
  ShieldCheck,
  Sun,
  Trophy,
  Users,
  X,
} from "lucide-react";
import { useApi } from "../hooks/useApi";
import { Avatar, SearchInput } from "./ui";

const NAV = [
  {
    group: "Overview",
    items: [{ to: "/", label: "Dashboard", icon: LayoutDashboard, end: true }],
  },
  {
    group: "Students",
    items: [{ to: "/leads", label: "Leads", icon: Users }],
  },
  {
    group: "Records",
    items: [
      { to: "/chats", label: "Conversations", icon: MessagesSquare },
      { to: "/feedbacks", label: "Feedback", icon: MessageSquareText },
      { to: "/notes", label: "Notes", icon: NotebookPen },
      { to: "/schedules", label: "Follow-ups", icon: CalendarClock },
      { to: "/exams", label: "Exams", icon: Trophy },
      { to: "/grades", label: "Grades", icon: GraduationCap },
    ],
  },
  {
    group: "Knowledge",
    items: [{ to: "/resources", label: "Resources", icon: BookOpen }],
  },
];

function useTheme() {
  const [theme, setTheme] = useState(() => {
    try {
      return localStorage.getItem("crm-theme") || "system";
    } catch {
      return "system";
    }
  });

  useEffect(() => {
    const root = document.documentElement;
    if (theme === "system") root.removeAttribute("data-theme");
    else root.setAttribute("data-theme", theme);
    try {
      localStorage.setItem("crm-theme", theme);
    } catch {
      // storage unavailable, theme still applies for this visit
    }
  }, [theme]);

  const isDark =
    theme === "dark" || (theme === "system" && window.matchMedia?.("(prefers-color-scheme: dark)").matches);

  return [isDark, () => setTheme(isDark ? "light" : "dark")];
}

export default function Layout() {
  const [menuOpen, setMenuOpen] = useState(false);
  const [isDark, toggleTheme] = useTheme();
  const location = useLocation();
  const navigate = useNavigate();
  const admin = useApi("/admin");

  useEffect(() => setMenuOpen(false), [location.pathname]);

  return (
    <div className={`shell ${menuOpen ? "menu-open" : ""}`}>
      <aside className="sidebar">
        <div className="sidebar__brand">
          <span className="brand-mark">
            <AudioLines size={18} />
          </span>
          <div>
            <strong>SmartAgent</strong>
            <span>Admissions CRM</span>
          </div>
          <button className="icon-button sidebar__close" onClick={() => setMenuOpen(false)} aria-label="Close menu">
            <X size={18} />
          </button>
        </div>

        <nav className="sidebar__nav">
          {NAV.map((section) => (
            <div key={section.group} className="nav-group">
              <span className="nav-group__label">{section.group}</span>
              {section.items.map((item) => (
                <NavLink key={item.to} to={item.to} end={item.end} className="nav-link">
                  <item.icon size={17} />
                  {item.label}
                </NavLink>
              ))}
            </div>
          ))}
        </nav>

        <NavLink to="/admin" className="sidebar__profile">
          <Avatar name={admin.data?.name || "Admin"} seed="admin" size="sm" />
          <span className="sidebar__profile-text">
            <strong>{admin.data?.name || "CRM Admin"}</strong>
            <span>
              <ShieldCheck size={12} /> {admin.data?.role || "ADMIN"}
            </span>
          </span>
        </NavLink>
      </aside>

      <div className="sidebar-backdrop" onClick={() => setMenuOpen(false)} />

      <div className="main">
        <header className="topbar">
          <button className="icon-button topbar__menu" onClick={() => setMenuOpen(true)} aria-label="Open menu">
            <Menu size={20} />
          </button>
          <div className="topbar__search">
            <SearchInput
              placeholder="Search leads by name, phone, email or city…"
              delay={500}
              onChange={(value) => value && navigate(`/leads?search=${encodeURIComponent(value)}`)}
            />
          </div>
          <div className="topbar__actions">
            <span className="read-only-pill" title="The CRM can only read data">
              <ClipboardList size={14} /> Read-only
            </span>
            <button className="icon-button" onClick={toggleTheme} aria-label="Toggle dark mode">
              {isDark ? <Sun size={18} /> : <Moon size={18} />}
            </button>
          </div>
        </header>

        <main className="content">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
