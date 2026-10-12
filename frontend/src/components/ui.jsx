import { useEffect, useRef, useState } from "react";
import { Link } from "react-router-dom";
import {
  AlertTriangle,
  ChevronLeft,
  ChevronRight,
  Inbox,
  RefreshCw,
  Search,
  ServerOff,
  X,
} from "lucide-react";
import { avatarHue, displayName, initials } from "../utils/format";

export function PageHeader({ icon: Icon, title, subtitle, actions }) {
  return (
    <header className="page-header">
      <div className="page-header__text">
        {Icon && (
          <span className="page-header__icon">
            <Icon size={20} />
          </span>
        )}
        <div>
          <h1>{title}</h1>
          {subtitle && <p>{subtitle}</p>}
        </div>
      </div>
      {actions && <div className="page-header__actions">{actions}</div>}
    </header>
  );
}

export function Card({ title, icon: Icon, action, children, className = "", padded = true }) {
  return (
    <section className={`card ${className}`}>
      {(title || action) && (
        <div className="card__header">
          <h2>
            {Icon && <Icon size={16} />}
            {title}
          </h2>
          {action}
        </div>
      )}
      <div className={padded ? "card__body" : ""}>{children}</div>
    </section>
  );
}

export function Badge({ children, tone = "neutral", icon: Icon }) {
  return (
    <span className={`badge badge--${tone}`}>
      {Icon && <Icon size={12} />}
      {children}
    </span>
  );
}

export function Avatar({ name, seed, size = "md" }) {
  const hue = avatarHue(seed ?? name);
  return (
    <span className={`avatar avatar--${size}`} style={{ "--hue": hue }} aria-hidden="true">
      {initials(name, "#")}
    </span>
  );
}

// avatar + name + phone, links to the lead page
export function LeadCell({ lead, compact = false }) {
  if (!lead) return <span className="muted">—</span>;
  return (
    <Link to={`/leads/${lead.id}`} className="lead-cell" onClick={(event) => event.stopPropagation()}>
      <Avatar name={lead.name} seed={lead.id} size={compact ? "sm" : "md"} />
      <span className="lead-cell__text">
        <span className={`lead-cell__name ${lead.name ? "" : "muted"}`}>{displayName(lead)}</span>
        {!compact && <span className="lead-cell__phone">{lead.phoneNumber}</span>}
      </span>
    </Link>
  );
}

export function Skeleton({ width = "100%", height = 14, radius = 6, style }) {
  return <span className="skeleton" style={{ width, height, borderRadius: radius, ...style }} />;
}

export function SkeletonRows({ rows = 5, columns = 4 }) {
  return (
    <div className="skeleton-rows">
      {Array.from({ length: rows }).map((_, row) => (
        <div className="skeleton-row" key={row}>
          {Array.from({ length: columns }).map((__, column) => (
            <Skeleton key={column} width={column === 0 ? "70%" : "50%"} />
          ))}
        </div>
      ))}
    </div>
  );
}

export function EmptyState({ icon: Icon = Inbox, title = "Nothing here yet", message, action }) {
  return (
    <div className="empty-state">
      <span className="empty-state__icon">
        <Icon size={22} />
      </span>
      <h3>{title}</h3>
      {message && <p>{message}</p>}
      {action}
    </div>
  );
}

export function ErrorState({ error, onRetry }) {
  const offline = error?.status === 0;
  return (
    <div className="empty-state empty-state--error">
      <span className="empty-state__icon">{offline ? <ServerOff size={22} /> : <AlertTriangle size={22} />}</span>
      <h3>{offline ? "CRM server is not reachable" : "Couldn't load this data"}</h3>
      <p>{error?.message}</p>
      {offline && (
        <p className="empty-state__hint">
          Start the backend with <code>mvn compile exec:java "-Dexec.mainClass=Main"</code> and make sure{" "}
          <code>HttpServer</code> is started on port 8081.
        </p>
      )}
      {onRetry && (
        <button className="button button--secondary" onClick={onRetry}>
          <RefreshCw size={14} /> Try again
        </button>
      )}
    </div>
  );
}

// search box that updates its parent after the user stops typing
export function SearchInput({ value, onChange, placeholder = "Search…", delay = 300, autoFocus = false }) {
  const [text, setText] = useState(value || "");
  const timer = useRef();

  useEffect(() => setText(value || ""), [value]);
  useEffect(() => () => clearTimeout(timer.current), []);

  const update = (next) => {
    setText(next);
    clearTimeout(timer.current);
    timer.current = setTimeout(() => onChange(next.trim()), delay);
  };

  return (
    <label className="search-input">
      <Search size={16} />
      <input
        type="search"
        value={text}
        placeholder={placeholder}
        autoFocus={autoFocus}
        onChange={(event) => update(event.target.value)}
      />
      {text && (
        <button type="button" className="search-input__clear" aria-label="Clear search" onClick={() => update("")}>
          <X size={14} />
        </button>
      )}
    </label>
  );
}

export function Pagination({ page, totalPages, total, size, onPageChange, onSizeChange }) {
  if (!total) return null;
  const first = (page - 1) * size + 1;
  const last = Math.min(page * size, total);

  return (
    <div className="pagination">
      <span className="pagination__info">
        Showing <strong>{first}</strong>–<strong>{last}</strong> of <strong>{total}</strong>
      </span>
      <div className="pagination__controls">
        {onSizeChange && (
          <select value={size} onChange={(event) => onSizeChange(Number(event.target.value))} aria-label="Rows per page">
            {[10, 20, 50, 100].map((option) => (
              <option key={option} value={option}>
                {option} / page
              </option>
            ))}
          </select>
        )}
        <button
          className="icon-button"
          disabled={page <= 1}
          onClick={() => onPageChange(page - 1)}
          aria-label="Previous page"
        >
          <ChevronLeft size={16} />
        </button>
        <span className="pagination__page">
          {page} / {Math.max(totalPages, 1)}
        </span>
        <button
          className="icon-button"
          disabled={page >= totalPages}
          onClick={() => onPageChange(page + 1)}
          aria-label="Next page"
        >
          <ChevronRight size={16} />
        </button>
      </div>
    </div>
  );
}

// columns: [{ key, header, render(row), align, width }]
export function DataTable({ columns, rows, onRowClick, loading, empty }) {
  if (loading && !rows) return <SkeletonRows columns={Math.min(columns.length, 5)} />;
  if (rows && rows.length === 0) return empty;

  return (
    <div className={`table-wrap ${loading ? "is-refreshing" : ""}`}>
      <table className="table">
        <thead>
          <tr>
            {columns.map((column) => (
              <th key={column.key} style={{ width: column.width, textAlign: column.align }}>
                {column.header}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows?.map((row) => (
            <tr
              key={row.id}
              onClick={onRowClick ? () => onRowClick(row) : undefined}
              className={onRowClick ? "is-clickable" : ""}
            >
              {columns.map((column) => (
                <td key={column.key} style={{ textAlign: column.align }} data-label={column.header}>
                  {column.render ? column.render(row) : row[column.key] ?? <span className="muted">—</span>}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export function Tabs({ tabs, active, onChange }) {
  return (
    <div className="tabs" role="tablist">
      {tabs.map((tab) => (
        <button
          key={tab.id}
          role="tab"
          aria-selected={active === tab.id}
          className={`tabs__tab ${active === tab.id ? "is-active" : ""}`}
          onClick={() => onChange(tab.id)}
        >
          {tab.icon && <tab.icon size={15} />}
          {tab.label}
          {tab.count !== undefined && <span className="tabs__count">{tab.count}</span>}
        </button>
      ))}
    </div>
  );
}

export function Modal({ open, onClose, title, children, wide = false }) {
  useEffect(() => {
    if (!open) return undefined;
    const onKey = (event) => event.key === "Escape" && onClose();
    document.addEventListener("keydown", onKey);
    document.body.style.overflow = "hidden";
    return () => {
      document.removeEventListener("keydown", onKey);
      document.body.style.overflow = "";
    };
  }, [open, onClose]);

  if (!open) return null;

  return (
    <div className="modal-backdrop" onMouseDown={onClose}>
      <div
        className={`modal ${wide ? "modal--wide" : ""}`}
        role="dialog"
        aria-modal="true"
        onMouseDown={(event) => event.stopPropagation()}
      >
        <div className="modal__header">
          <h2>{title}</h2>
          <button className="icon-button" onClick={onClose} aria-label="Close">
            <X size={18} />
          </button>
        </div>
        <div className="modal__body">{children}</div>
      </div>
    </div>
  );
}

export function StatCard({ icon: Icon, label, value, tone = "indigo", to, loading }) {
  const content = (
    <>
      <span className={`stat-card__icon tone-${tone}`}>
        <Icon size={18} />
      </span>
      <span className="stat-card__label">{label}</span>
      <span className="stat-card__value">{loading ? <Skeleton width={48} height={26} /> : value}</span>
    </>
  );
  return to ? (
    <Link to={to} className="stat-card is-clickable">
      {content}
    </Link>
  ) : (
    <div className="stat-card">{content}</div>
  );
}
