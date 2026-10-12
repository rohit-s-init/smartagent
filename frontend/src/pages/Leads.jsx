import { useNavigate } from "react-router-dom";
import {
  CalendarClock,
  GraduationCap,
  MapPin,
  MessageSquareText,
  MessagesSquare,
  NotebookPen,
  Trophy,
  Users,
} from "lucide-react";
import { useApi } from "../hooks/useApi";
import { usePaging } from "../hooks/useQueryState";
import { Badge, Card, DataTable, EmptyState, ErrorState, LeadCell, PageHeader, Pagination, SearchInput } from "../components/ui";
import { formatTime } from "../utils/format";

function CountPill({ icon: Icon, value, label }) {
  return (
    <span className={`count-pill ${value ? "" : "is-zero"}`} title={`${value} ${label}`}>
      <Icon size={13} />
      {value}
    </span>
  );
}

const COLUMNS = [
  { key: "lead", header: "Student", render: (lead) => <LeadCell lead={lead} />, width: "26%" },
  {
    key: "location",
    header: "Location",
    render: (lead) =>
      lead.location ? (
        <span className="with-icon">
          <MapPin size={14} /> {lead.location}
        </span>
      ) : (
        <span className="muted">—</span>
      ),
  },
  {
    key: "schooling",
    header: "Schooling",
    render: (lead) => (lead.schooling ? <Badge tone="indigo">{lead.schooling}</Badge> : <span className="muted">—</span>),
  },
  { key: "email", header: "Email", render: (lead) => lead.email || <span className="muted">—</span> },
  {
    key: "activity",
    header: "Activity",
    render: (lead) => (
      <span className="count-pills">
        <CountPill icon={MessagesSquare} value={lead.counts.chats} label="chat messages" />
        <CountPill icon={MessageSquareText} value={lead.counts.feedbacks} label="feedbacks" />
        <CountPill icon={Trophy} value={lead.counts.exams} label="exams" />
        <CountPill icon={GraduationCap} value={lead.counts.grades} label="grades" />
      </span>
    ),
  },
  {
    key: "followUp",
    header: "Follow-up",
    render: (lead) => (
      <span className="follow-up">
        {lead.scheduledTime ? (
          <Badge tone="amber" icon={CalendarClock}>
            {formatTime(lead.scheduledTime)}
          </Badge>
        ) : (
          <span className="muted">—</span>
        )}
        {lead.hasNote && (
          <span className="note-dot" title="Has a note">
            <NotebookPen size={14} />
          </span>
        )}
      </span>
    ),
  },
];

export default function Leads() {
  const navigate = useNavigate();
  const { page, size, get, set } = usePaging(20);
  const search = get("search");
  const { data, error, loading, reload } = useApi("/leads", { search, page, size });

  return (
    <>
      <PageHeader
        icon={Users}
        title="Leads"
        subtitle={data ? `${data.total} student${data.total === 1 ? "" : "s"} the agent has spoken to` : "Students the agent has spoken to"}
      />

      <Card padded={false}>
        <div className="toolbar">
          <SearchInput
            value={search}
            placeholder="Search by name, phone, email or city"
            onChange={(value) => set({ search: value, page: null })}
          />
        </div>

        {error ? (
          <ErrorState error={error} onRetry={reload} />
        ) : (
          <>
            <DataTable
              columns={COLUMNS}
              rows={data?.items}
              loading={loading}
              onRowClick={(lead) => navigate(`/leads/${lead.id}`)}
              empty={
                <EmptyState
                  icon={Users}
                  title={search ? `No leads match “${search}”` : "No leads yet"}
                  message={
                    search
                      ? "Try a different name, phone number, email or city."
                      : "A lead is created the first time the agent talks to a student."
                  }
                />
              }
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
