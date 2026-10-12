import { Link } from "react-router-dom";
import {
  ArrowRight,
  BookOpen,
  CalendarClock,
  GraduationCap,
  LayoutDashboard,
  MessageSquareText,
  MessagesSquare,
  NotebookPen,
  Trophy,
  Users,
} from "lucide-react";
import { useApi } from "../hooks/useApi";
import { Card, EmptyState, ErrorState, LeadCell, PageHeader, Skeleton, SkeletonRows, StatCard } from "../components/ui";
import { formatNumber, formatTime, timeAgo, titleCase } from "../utils/format";

const STATS = [
  { key: "leads", label: "Leads", icon: Users, tone: "indigo", to: "/leads" },
  { key: "chats", label: "Chat messages", icon: MessagesSquare, tone: "sky", to: "/chats" },
  { key: "feedbacks", label: "Feedback", icon: MessageSquareText, tone: "violet", to: "/feedbacks" },
  { key: "schedules", label: "Follow-ups", icon: CalendarClock, tone: "amber", to: "/schedules" },
  { key: "notes", label: "Notes", icon: NotebookPen, tone: "teal", to: "/notes" },
  { key: "exams", label: "Exams", icon: Trophy, tone: "rose", to: "/exams" },
  { key: "grades", label: "Grades", icon: GraduationCap, tone: "emerald", to: "/grades" },
  { key: "resources", label: "Resources", icon: BookOpen, tone: "slate", to: "/resources" },
];

function greeting() {
  const hour = new Date().getHours();
  if (hour < 12) return "Good morning";
  if (hour < 17) return "Good afternoon";
  return "Good evening";
}

function SeeAll({ to }) {
  return (
    <Link to={to} className="link-button">
      See all <ArrowRight size={14} />
    </Link>
  );
}

export default function Dashboard() {
  const { data, error, loading, reload } = useApi("/dashboard");
  const admin = useApi("/admin");

  if (error) {
    return (
      <>
        <PageHeader icon={LayoutDashboard} title="Dashboard" />
        <ErrorState error={error} onRetry={reload} />
      </>
    );
  }

  const totals = data?.totals || {};
  const categories = Object.entries(data?.resourcesByCategory || {}).sort((a, b) => b[1] - a[1]);
  const maxCategory = Math.max(1, ...categories.map(([, count]) => count));

  return (
    <>
      <PageHeader
        icon={LayoutDashboard}
        title={`${greeting()}, ${admin.data?.name || "Admin"}`}
        subtitle="Here's what your voice agent has collected from students so far."
      />

      <div className="stat-grid">
        {STATS.map((stat) => (
          <StatCard
            key={stat.key}
            icon={stat.icon}
            label={stat.label}
            tone={stat.tone}
            to={stat.to}
            loading={loading}
            value={formatNumber(totals[stat.key])}
          />
        ))}
      </div>

      <div className="dashboard-grid">
        <Card title="Newest leads" icon={Users} action={<SeeAll to="/leads" />} className="span-2" padded={false}>
          {loading ? (
            <SkeletonRows rows={4} columns={3} />
          ) : data.recentLeads.length === 0 ? (
            <EmptyState title="No leads yet" message="Leads appear here as soon as the agent talks to a student." />
          ) : (
            <ul className="list">
              {data.recentLeads.map((lead) => (
                <li key={lead.id} className="list__item">
                  <LeadCell lead={lead} />
                  <span className="list__meta">
                    {[lead.location, lead.schooling].filter(Boolean).join(" · ") || "Profile not filled yet"}
                  </span>
                </li>
              ))}
            </ul>
          )}
        </Card>

        <Card title="Upcoming follow-ups" icon={CalendarClock} action={<SeeAll to="/schedules" />} padded={false}>
          {loading ? (
            <SkeletonRows rows={3} columns={2} />
          ) : data.upcomingSchedules.length === 0 ? (
            <EmptyState
              icon={CalendarClock}
              title="No follow-ups scheduled"
              message="When a student asks for a call back, it shows up here."
            />
          ) : (
            <ul className="list">
              {data.upcomingSchedules.map((schedule) => (
                <li key={schedule.id} className="list__item">
                  <LeadCell lead={schedule.lead} compact />
                  <span className="time-chip">{formatTime(schedule.scheduledTime)}</span>
                </li>
              ))}
            </ul>
          )}
        </Card>

        <Card title="Latest feedback" icon={MessageSquareText} action={<SeeAll to="/feedbacks" />} className="span-2" padded={false}>
          {loading ? (
            <SkeletonRows rows={3} columns={2} />
          ) : data.recentFeedbacks.length === 0 ? (
            <EmptyState
              icon={MessageSquareText}
              title="No feedback yet"
              message="Feedback the agent records during calls will be listed here."
            />
          ) : (
            <ul className="feed">
              {data.recentFeedbacks.map((feedback) => (
                <li key={feedback.id} className="feed__item">
                  <p>{feedback.message}</p>
                  <div className="feed__meta">
                    <LeadCell lead={feedback.lead} compact />
                    <span>{timeAgo(feedback.createdAt)}</span>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </Card>

        <Card title="Knowledge base" icon={BookOpen} action={<SeeAll to="/resources" />}>
          {loading ? (
            <div className="bars">
              {[1, 2, 3, 4].map((key) => (
                <Skeleton key={key} height={28} />
              ))}
            </div>
          ) : categories.length === 0 ? (
            <EmptyState icon={BookOpen} title="No resources" />
          ) : (
            <div className="bars">
              {categories.map(([category, count]) => (
                <Link key={category} to={`/resources?category=${category}`} className="bar">
                  <span className="bar__label">
                    {titleCase(category)}
                    <strong>{count}</strong>
                  </span>
                  <span className="bar__track">
                    <span className="bar__fill" style={{ width: `${(count / maxCategory) * 100}%` }} />
                  </span>
                </Link>
              ))}
            </div>
          )}
        </Card>
      </div>
    </>
  );
}
