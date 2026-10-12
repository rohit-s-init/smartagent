import { Bot, MessagesSquare } from "lucide-react";
import { Avatar, EmptyState } from "./ui";
import { formatDateTime, isStudentRole } from "../utils/format";

// chat transcript, student on the right, voice agent on the left
export default function Conversation({ chats, lead }) {
  if (!chats?.length) {
    return (
      <EmptyState
        icon={MessagesSquare}
        title="No conversation yet"
        message="Messages between the student and the voice agent will appear here."
      />
    );
  }

  return (
    <ol className="transcript">
      {chats.map((chat) => {
        const student = isStudentRole(chat.role);
        return (
          <li key={chat.id} className={`bubble-row ${student ? "is-student" : "is-agent"}`}>
            {student ? (
              <Avatar name={lead?.name} seed={lead?.id} size="sm" />
            ) : (
              <span className="agent-avatar">
                <Bot size={15} />
              </span>
            )}
            <div className="bubble">
              <p>{chat.message}</p>
              <span className="bubble__meta">
                {student ? lead?.name || "Student" : "Voice agent"} · {formatDateTime(chat.createdAt)}
              </span>
            </div>
          </li>
        );
      })}
    </ol>
  );
}
