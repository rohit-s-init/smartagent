import { Link } from "react-router-dom";
import { Compass } from "lucide-react";
import { EmptyState } from "../components/ui";

export default function NotFound() {
  return (
    <EmptyState
      icon={Compass}
      title="Page not found"
      message="This page doesn't exist in the CRM."
      action={
        <Link to="/" className="button button--secondary">
          Go to dashboard
        </Link>
      }
    />
  );
}
