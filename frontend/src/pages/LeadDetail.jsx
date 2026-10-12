import { useState } from "react";
import { Link, useParams } from "react-router-dom";
import {
  ArrowLeft,
  CalendarClock,
  GraduationCap,
  LayoutGrid,
  Mail,
  MapPin,
  MessageSquareText,
  MessagesSquare,
  NotebookPen,
  Phone,
  School,
  Trophy,
  UserX,
} from "lucide-react";
import { useApi } from "../hooks/useApi";
import Conversation from "../components/Conversation";
import { Avatar, Badge, Card, EmptyState, ErrorState, Skeleton, SkeletonRows, Tabs } from "../components/ui";
import { displayName, formatDateTime, formatScore, formatTime, timeAgo } from "../utils/format";

const PROFILE_FIELDS = [
  ["name", "Name"],
  ["email", "Email"],
  ["location", "Location"],
  ["schooling", "Schooling"],
];

function ProfileCompleteness({ lead }) {
  const filled = PROFILE_FIELDS.filter(([key]) => lead[key]).length;
  const percent = Math.round((filled / PROFILE_FIELDS.length) * 100);
  const missing = PROFILE_FIELDS.filter(([key]) => !lead[key]).map(([, label]) => label);

  return (
    <div className="completeness">
      <div className="completeness__head">
        <span>Profile completeness</span>
        <strong>{percent}%</strong>
      </div>
      <span className="completeness__track">
        <span className="completeness__fill" style={{ width: `${percent}%` }} />
      </span>
      <span className="completeness__hint">
        {missing.length ? `Agent still needs: ${missing.join(", ")}` : "The agent has collected every profile detail."}
      </span>
    </div>
  );
}

function InfoRow({ icon: Icon, label, value, href }) {
  const content = value || <span className="muted">Not collected</span>;
  return (
    <div className="info-row">
      <span className="info-row__icon">
        <Icon size={15} />
      </span>
      <span className="info-row__label">{label}</span>
      <span className="info-row__value">{href && value ? <a href={href}>{value}</a> : content}</span>
    </div>
  );
}

function ExamsTable({ exams }) {
  if (!exams.length) return <EmptyState icon={Trophy} title="No exams recorded" />;
  return (
    <ul className="score-list">
      {exams.map((exam) => (
        <li key={exam.id}>
          <span className="score-list__name">{exam.examName || "Unnamed exam"}</span>
          <span className="score-list__value">{formatScore(exam.score, exam.scoreType)}</span>
        </li>
      ))}
    </ul>
  );
}

function GradesTable({ grades }) {
  if (!grades.length) return <EmptyState icon={GraduationCap} title="No grades recorded" />;
  return (
    <ul className="score-list">
      {grades.map((grade) => (
        <li key={grade.id}>
          <span className="score-list__name">{grade.qualification || "Qualification"}</span>
          <span className="score-list__value">{grade.percentage != null ? `${grade.percentage}%` : "—"}</span>
        </li>
      ))}
    </ul>
  );
}

function FeedbackTimeline({ feedbacks }) {
  if (!feedbacks.length) {
    return (
      <EmptyState
        icon={MessageSquareText}
        title="No feedback yet"
        message="Feedback the agent records for this student will show up here."
      />
    );
  }
  return (
    <ol className="timeline">
      {feedbacks.map((feedback) => (
        <li key={feedback.id} className="timeline__item">
          <span className="timeline__dot" />
          <div>
            <p>{feedback.message}</p>
            <span className="timeline__meta" title={formatDateTime(feedback.createdAt)}>
              {timeAgo(feedback.createdAt) || formatDateTime(feedback.createdAt)}
            </span>
          </div>
        </li>
      ))}
    </ol>
  );
}

function LoadingDetail() {
  return (
    <div className="detail-grid">
      <Card>
        <div className="profile-head">
          <Skeleton width={64} height={64} radius={20} />
          <div style={{ flex: 1, display: "grid", gap: 8 }}>
            <Skeleton width="50%" height={20} />
            <Skeleton width="30%" />
          </div>
        </div>
        <SkeletonRows rows={4} columns={2} />
      </Card>
      <Card>
        <SkeletonRows rows={6} columns={2} />
      </Card>
    </div>
  );
}

export default function LeadDetail() {
  const { id } = useParams();
  const { data: lead, error, loading, reload } = useApi(`/leads/${id}`);
  const [tab, setTab] = useState("overview");

  const back = (
    <Link to="/leads" className="back-link">
      <ArrowLeft size={16} /> All leads
    </Link>
  );

  if (error) {
    return (
      <>
        {back}
        {error.status === 404 || error.status === 400 ? (
          <EmptyState
            icon={UserX}
            title="Lead not found"
            message={error.message}
            action={
              <Link to="/leads" className="button button--secondary">
                Back to leads
              </Link>
            }
          />
        ) : (
          <ErrorState error={error} onRetry={reload} />
        )}
      </>
    );
  }

  if (loading || !lead) {
    return (
      <>
        {back}
        <LoadingDetail />
      </>
    );
  }

  const tabs = [
    { id: "overview", label: "Overview", icon: LayoutGrid },
    { id: "conversation", label: "Conversation", icon: MessagesSquare, count: lead.chats.length },
    { id: "feedback", label: "Feedback", icon: MessageSquareText, count: lead.feedbacks.length },
    { id: "academics", label: "Academics", icon: GraduationCap, count: lead.exams.length + lead.grades.length },
  ];

  return (
    <>
      {back}

      <div className="detail-grid">
        <aside className="detail-side">
          <Card>
            <div className="profile-head">
              <Avatar name={lead.name} seed={lead.id} size="xl" />
              <div>
                <h1 className={lead.name ? "" : "muted"}>{displayName(lead)}</h1>
                <span className="profile-head__sub">Lead #{lead.id}</span>
              </div>
            </div>

            <div className="info-rows">
              <InfoRow icon={Phone} label="Phone" value={lead.phoneNumber} href={`tel:${lead.phoneNumber}`} />
              <InfoRow icon={Mail} label="Email" value={lead.email} href={lead.email ? `mailto:${lead.email}` : undefined} />
              <InfoRow icon={MapPin} label="Location" value={lead.location} />
              <InfoRow icon={School} label="Schooling" value={lead.schooling} />
            </div>

            <ProfileCompleteness lead={lead} />
          </Card>

          <Card title="Follow-up call" icon={CalendarClock}>
            {lead.schedule ? (
              <div className="follow-up-card">
                <span className="follow-up-card__time">{formatTime(lead.schedule.scheduledTime)}</span>
                <span className="muted">Scheduled by the voice agent</span>
              </div>
            ) : (
              <p className="muted small">No follow-up call scheduled.</p>
            )}
          </Card>

          <Card title="Agent's note" icon={NotebookPen}>
            {lead.note ? (
              <div className="note-card">
                <p>{lead.note.content}</p>
                <span className="muted small">Saved {formatDateTime(lead.note.createdAt)}</span>
              </div>
            ) : (
              <p className="muted small">The agent hasn't written a note for this student yet.</p>
            )}
          </Card>
        </aside>

        <section className="detail-main">
          <div className="mini-stats">
            <div>
              <MessagesSquare size={16} />
              <strong>{lead.chats.length}</strong>
              <span>Messages</span>
            </div>
            <div>
              <MessageSquareText size={16} />
              <strong>{lead.feedbacks.length}</strong>
              <span>Feedback</span>
            </div>
            <div>
              <Trophy size={16} />
              <strong>{lead.exams.length}</strong>
              <span>Exams</span>
            </div>
            <div>
              <GraduationCap size={16} />
              <strong>{lead.grades.length}</strong>
              <span>Grades</span>
            </div>
          </div>

          <Card padded={false}>
            <Tabs tabs={tabs} active={tab} onChange={setTab} />
            <div className="tab-panel">
              {tab === "overview" && (
                <div className="overview-grid">
                  <div>
                    <h3 className="section-title">
                      <Trophy size={15} /> Entrance exams
                    </h3>
                    <ExamsTable exams={lead.exams} />
                  </div>
                  <div>
                    <h3 className="section-title">
                      <GraduationCap size={15} /> Academic grades
                    </h3>
                    <GradesTable grades={lead.grades} />
                  </div>
                  <div className="span-all">
                    <h3 className="section-title">
                      <MessageSquareText size={15} /> Latest feedback
                    </h3>
                    <FeedbackTimeline feedbacks={lead.feedbacks.slice(0, 3)} />
                  </div>
                  <div className="span-all">
                    <h3 className="section-title">
                      <MessagesSquare size={15} /> Recent conversation
                    </h3>
                    <Conversation chats={lead.chats.slice(-4)} lead={lead} />
                    {lead.chats.length > 4 && (
                      <button className="link-button" onClick={() => setTab("conversation")}>
                        View full conversation ({lead.chats.length} messages)
                      </button>
                    )}
                  </div>
                </div>
              )}

              {tab === "conversation" && <Conversation chats={lead.chats} lead={lead} />}

              {tab === "feedback" && <FeedbackTimeline feedbacks={lead.feedbacks} />}

              {tab === "academics" && (
                <div className="overview-grid">
                  <div>
                    <h3 className="section-title">
                      <Trophy size={15} /> Entrance exams <Badge>{lead.exams.length}</Badge>
                    </h3>
                    <ExamsTable exams={lead.exams} />
                  </div>
                  <div>
                    <h3 className="section-title">
                      <GraduationCap size={15} /> Academic grades <Badge>{lead.grades.length}</Badge>
                    </h3>
                    <GradesTable grades={lead.grades} />
                  </div>
                </div>
              )}
            </div>
          </Card>
        </section>
      </div>
    </>
  );
}
