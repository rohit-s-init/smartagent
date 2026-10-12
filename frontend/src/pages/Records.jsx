import { CalendarClock, Filter, GraduationCap, MessageSquareText, NotebookPen, Trophy, X } from "lucide-react";
import { useApi } from "../hooks/useApi";
import { usePaging } from "../hooks/useQueryState";
import { Badge, Card, DataTable, EmptyState, ErrorState, LeadCell, PageHeader, Pagination } from "../components/ui";
import { formatDateTime, formatScore, formatTime, timeAgo } from "../utils/format";

// shared page for the global record lists (?leadId= narrows it to one student)
export function RecordPage({ path, icon, title, subtitle, columns, emptyTitle, emptyMessage, defaultSize = 20 }) {
  const { page, size, get, set } = usePaging(defaultSize);
  const leadId = get("leadId");
  const { data, error, loading, reload } = useApi(path, { leadId, page, size });

  const visibleColumns = leadId ? columns.filter((column) => column.key !== "lead") : columns;

  return (
    <>
      <PageHeader
        icon={icon}
        title={title}
        subtitle={data ? `${data.total} record${data.total === 1 ? "" : "s"} · ${subtitle}` : subtitle}
      />

      <Card padded={false}>
        {leadId && (
          <div className="toolbar">
            <span className="filter-chip">
              <Filter size={13} /> Lead #{leadId}
              <button aria-label="Clear filter" onClick={() => set({ leadId: null, page: null })}>
                <X size={13} />
              </button>
            </span>
          </div>
        )}

        {error ? (
          <ErrorState error={error} onRetry={reload} />
        ) : (
          <>
            <DataTable
              columns={visibleColumns}
              rows={data?.items}
              loading={loading}
              empty={<EmptyState icon={icon} title={emptyTitle} message={emptyMessage} />}
            />
            {data && (
              <Pagination
                page={data.page}
                size={data.size}
                total={data.total}
                totalPages={data.totalPages}
                onPageChange={(next) => set({ page: next })}
                onSizeChange={(next) => set({ size: next, page: null })}
              />
            )}
          </>
        )}
      </Card>
    </>
  );
}

const leadColumn = { key: "lead", header: "Student", render: (row) => <LeadCell lead={row.lead} />, width: "28%" };

const createdColumn = {
  key: "createdAt",
  header: "Recorded",
  render: (row) => (
    <span className="stacked" title={formatDateTime(row.createdAt)}>
      <span>{formatDateTime(row.createdAt)}</span>
      <span className="muted small">{timeAgo(row.createdAt)}</span>
    </span>
  ),
  width: "20%",
};

export function Exams() {
  return (
    <RecordPage
      path="/exams"
      icon={Trophy}
      title="Exams"
      subtitle="entrance exam results shared by students"
      emptyTitle="No exam results yet"
      emptyMessage="When a student tells the agent their JEE, CET or other scores, they are listed here."
      columns={[
        leadColumn,
        { key: "examName", header: "Exam", render: (row) => <strong>{row.examName || "—"}</strong> },
        { key: "score", header: "Score", render: (row) => <Badge tone="rose">{formatScore(row.score, row.scoreType)}</Badge> },
        { key: "scoreType", header: "Score type", render: (row) => row.scoreType || <span className="muted">—</span> },
      ]}
    />
  );
}

export function Grades() {
  return (
    <RecordPage
      path="/grades"
      icon={GraduationCap}
      title="Grades"
      subtitle="10th / 12th / diploma results"
      emptyTitle="No grades yet"
      emptyMessage="Academic percentages students share with the agent appear here."
      columns={[
        leadColumn,
        { key: "qualification", header: "Qualification", render: (row) => <strong>{row.qualification || "—"}</strong> },
        {
          key: "percentage",
          header: "Percentage",
          render: (row) =>
            row.percentage != null ? (
              <span className="percent">
                <span className="percent__track">
                  <span className="percent__fill" style={{ width: `${Math.min(100, row.percentage)}%` }} />
                </span>
                {row.percentage}%
              </span>
            ) : (
              <span className="muted">—</span>
            ),
        },
      ]}
    />
  );
}

export function Feedbacks() {
  return (
    <RecordPage
      path="/feedbacks"
      icon={MessageSquareText}
      title="Feedback"
      subtitle="newest first"
      emptyTitle="No feedback yet"
      emptyMessage="Feedback the agent records during calls will be listed here."
      columns={[
        leadColumn,
        { key: "message", header: "Feedback", render: (row) => <span className="clamp-2">{row.message}</span> },
        createdColumn,
      ]}
    />
  );
}

export function Notes() {
  return (
    <RecordPage
      path="/notes"
      icon={NotebookPen}
      title="Notes"
      subtitle="one running note per student"
      emptyTitle="No notes yet"
      emptyMessage="The agent writes a note per student summarising what it learned."
      columns={[
        leadColumn,
        { key: "content", header: "Note", render: (row) => <span className="clamp-2">{row.content}</span> },
        createdColumn,
      ]}
    />
  );
}

export function Schedules() {
  return (
    <RecordPage
      path="/schedules"
      icon={CalendarClock}
      title="Follow-ups"
      subtitle="call-backs, earliest first"
      emptyTitle="No follow-ups scheduled"
      emptyMessage="When a student asks to be called back, the agent schedules it and it appears here."
      columns={[
        leadColumn,
        {
          key: "scheduledTime",
          header: "Call at",
          render: (row) => (
            <Badge tone="amber" icon={CalendarClock}>
              {formatTime(row.scheduledTime)}
            </Badge>
          ),
        },
        {
          key: "phone",
          header: "Phone",
          render: (row) =>
            row.lead?.phoneNumber ? (
              <a href={`tel:${row.lead.phoneNumber}`} onClick={(event) => event.stopPropagation()}>
                {row.lead.phoneNumber}
              </a>
            ) : (
              <span className="muted">—</span>
            ),
        },
      ]}
    />
  );
}
