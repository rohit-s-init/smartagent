import { Link } from "react-router-dom";
import { Bot, Filter, MessagesSquare, X } from "lucide-react";
import { useApi } from "../hooks/useApi";
import { usePaging } from "../hooks/useQueryState";
import Conversation from "../components/Conversation";
import { Avatar, Card, EmptyState, ErrorState, LeadCell, PageHeader, Pagination, SkeletonRows } from "../components/ui";
import { displayName, formatDateTime, isStudentRole, timeAgo } from "../utils/format";

// activity feed of every message; with ?leadId= it becomes that student's transcript
export default function Chats() {
  const { page, size, get, set } = usePaging(30);
  const leadId = get("leadId");
  const { data, error, loading, reload } = useApi("/chats", { leadId, page, size });
  // filtered chats come without the lead, so load its name for the transcript
  const lead = useApi(`/leads/${leadId}`, {}, { enabled: Boolean(leadId) });
  const items = data?.items;

  return (
    <>
      <PageHeader
        icon={MessagesSquare}
        title="Conversations"
        subtitle={
          leadId
            ? "Transcript of one student, in the order it happened"
            : data
              ? `${data.total} message${data.total === 1 ? "" : "s"} across all calls, newest first`
              : "Every message across all calls, newest first"
        }
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
            <Link to={`/leads/${leadId}`} className="link-button">
              Open lead profile
            </Link>
          </div>
        )}

        {error ? (
          <ErrorState error={error} onRetry={reload} />
        ) : loading && !items ? (
          <SkeletonRows rows={6} columns={2} />
        ) : items.length === 0 ? (
          <EmptyState
            icon={MessagesSquare}
            title="No conversations yet"
            message="Every message between students and the voice agent is stored and shown here."
          />
        ) : leadId ? (
          <div className="tab-panel">
            <Conversation chats={items} lead={lead.data || { id: leadId }} />
          </div>
        ) : (
          <ul className="message-feed">
            {items.map((chat) => {
              const student = isStudentRole(chat.role);
              return (
                <li key={chat.id} className="message-feed__item">
                  {student ? (
                    <Avatar name={chat.lead?.name} seed={chat.lead?.id} size="sm" />
                  ) : (
                    <span className="agent-avatar">
                      <Bot size={15} />
                    </span>
                  )}
                  <div className="message-feed__body">
                    <div className="message-feed__head">
                      <strong>{student ? displayName(chat.lead) : "Voice agent"}</strong>
                      {!student && chat.lead && (
                        <span className="muted small">
                          to <LeadCell lead={chat.lead} compact />
                        </span>
                      )}
                      <span className="muted small" title={formatDateTime(chat.createdAt)}>
                        {timeAgo(chat.createdAt)}
                      </span>
                    </div>
                    <p>{chat.message}</p>
                    {chat.lead && (
                      <Link to={`/chats?leadId=${chat.lead.id}`} className="link-button small">
                        View conversation
                      </Link>
                    )}
                  </div>
                </li>
              );
            })}
          </ul>
        )}

        {data && !error && (
          <Pagination
            page={data.page}
            size={data.size}
            total={data.total}
            totalPages={data.totalPages}
            onPageChange={(next) => set({ page: next })}
            onSizeChange={(next) => set({ size: next, page: null })}
          />
        )}
      </Card>
    </>
  );
}
