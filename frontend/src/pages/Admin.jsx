import { CheckCircle2, KeyRound, Link2, Mail, ServerCog, ShieldCheck, UserCog } from "lucide-react";
import { API_BASE } from "../api";
import { useApi } from "../hooks/useApi";
import { Avatar, Badge, Card, ErrorState, PageHeader, Skeleton, SkeletonRows } from "../components/ui";

export default function Admin() {
  const admin = useApi("/admin");
  const index = useApi("/");
  const routes = Object.entries(index.data?.routes || {});

  return (
    <>
      <PageHeader icon={UserCog} title="Admin" subtitle="The account the CRM is used with, and the API it talks to" />

      <div className="admin-grid">
        <Card title="Account" icon={ShieldCheck}>
          {admin.error ? (
            <ErrorState error={admin.error} onRetry={admin.reload} />
          ) : admin.loading ? (
            <div style={{ display: "grid", gap: 10 }}>
              <Skeleton width={72} height={72} radius={22} />
              <Skeleton width="50%" height={20} />
              <Skeleton width="70%" />
            </div>
          ) : (
            <div className="admin-profile">
              <Avatar name={admin.data.name} seed="admin" size="xl" />
              <h2>{admin.data.name}</h2>
              <span className="muted">@{admin.data.username}</span>
              <div className="admin-profile__badges">
                <Badge tone="indigo" icon={ShieldCheck}>
                  {admin.data.role}
                </Badge>
                {admin.data.permissions.map((permission) => (
                  <Badge key={permission} tone="emerald" icon={CheckCircle2}>
                    {permission.replace(/_/g, " ")}
                  </Badge>
                ))}
              </div>
              <div className="info-rows">
                <div className="info-row">
                  <span className="info-row__icon">
                    <Mail size={15} />
                  </span>
                  <span className="info-row__label">Email</span>
                  <span className="info-row__value">{admin.data.email}</span>
                </div>
              </div>
              <div className="notice">
                <KeyRound size={16} />
                <span>
                  Sign-in is not enabled yet. Anyone who can reach the CRM can read all data, so don't share the URL
                  publicly.
                </span>
              </div>
            </div>
          )}
        </Card>

        <Card title="API connection" icon={ServerCog} padded={false}>
          <div className="api-status">
            <span className={`status-dot ${index.error ? "is-down" : index.loading ? "" : "is-up"}`} />
            <div>
              <strong>{index.error ? "Not reachable" : index.loading ? "Checking…" : "Connected"}</strong>
              <span className="muted small with-icon">
                <Link2 size={12} /> {API_BASE}
              </span>
            </div>
          </div>
          {index.error ? (
            <ErrorState error={index.error} onRetry={index.reload} />
          ) : index.loading ? (
            <SkeletonRows rows={6} columns={2} />
          ) : (
            <ul className="route-list">
              {routes.map(([route, description]) => {
                const [method, path] = route.split(" ");
                return (
                  <li key={route}>
                    <span className="route-list__method">{method}</span>
                    <code>{path}</code>
                    <span className="muted small">{description}</span>
                  </li>
                );
              })}
            </ul>
          )}
        </Card>
      </div>
    </>
  );
}
