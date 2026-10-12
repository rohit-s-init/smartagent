import { BookOpen, Building2, GraduationCap, Landmark, Layers, Trophy, Wallet } from "lucide-react";
import { useApi } from "../hooks/useApi";
import { usePaging } from "../hooks/useQueryState";
import { Badge, EmptyState, ErrorState, Modal, PageHeader, Pagination, SearchInput, Skeleton } from "../components/ui";
import { titleCase } from "../utils/format";

const CATEGORY_STYLE = {
  COURSE: { icon: GraduationCap, tone: "indigo" },
  ADMISSION: { icon: Landmark, tone: "sky" },
  SCHOLARSHIP: { icon: Wallet, tone: "emerald" },
  HOSTEL: { icon: Building2, tone: "amber" },
  PLACEMENT: { icon: Trophy, tone: "rose" },
};

function categoryStyle(category) {
  return CATEGORY_STYLE[(category || "").toUpperCase()] || { icon: Layers, tone: "slate" };
}

// all-caps labels written inline in the descriptions, e.g. "PROGRAMME:", "OFFICIAL 2026-27 FEE SCHEDULE:", "Sources:"
const HEADING = /([A-Z][A-Z0-9&/()\-–]+(?:[ \t]+[A-Z0-9][A-Z0-9&/()\-–]*)*|Sources):\s+/g;
const URL = /(https?:\/\/[^\s;,)]+)/g;

function splitSections(text) {
  const sections = [];
  let lastIndex = 0;
  let heading = null;

  for (const match of text.matchAll(HEADING)) {
    const body = text.slice(lastIndex, match.index).trim();
    if (body || heading) sections.push({ heading, body });
    heading = match[1];
    lastIndex = match.index + match[0].length;
  }
  sections.push({ heading, body: text.slice(lastIndex).trim() });
  return sections.filter((section) => section.heading || section.body);
}

function Linkified({ text }) {
  return text.split(URL).map((part, index) =>
    index % 2 === 1 ? (
      <a key={index} href={part} target="_blank" rel="noreferrer">
        {part.replace(/^https?:\/\//, "").replace(/\/$/, "")}
      </a>
    ) : (
      part
    )
  );
}

function SectionBody({ body }) {
  // "Year 1 Rs 2,87,000; Year 2 Rs 3,15,000; ..." and source links read better as a list
  const parts = body.split(/\s*;\s*/).filter(Boolean);
  if (parts.length >= 3 && parts.every((part) => part.length <= 160)) {
    return (
      <ul>
        {parts.map((part, index) => (
          <li key={index}>
            <Linkified text={part} />
          </li>
        ))}
      </ul>
    );
  }
  return (
    <p>
      <Linkified text={body} />
    </p>
  );
}

// turns the long single-paragraph description into titled sections
function ResourceDescription({ text }) {
  const sections = splitSections(text || "");
  if (!sections.length) return <p className="muted">No description.</p>;

  return (
    <div className="resource-text">
      {sections.map((section, index) =>
        section.heading ? (
          <section key={index}>
            <h4>{titleCase(section.heading)}</h4>
            <SectionBody body={section.body} />
          </section>
        ) : (
          <SectionBody key={index} body={section.body} />
        )
      )}
    </div>
  );
}

function ResourceModal({ id, onClose }) {
  const { data, error, loading } = useApi(`/resources/${id}`, {}, { enabled: Boolean(id) });
  const style = categoryStyle(data?.category);

  return (
    <Modal open={Boolean(id)} onClose={onClose} title={data?.title || "Resource"} wide>
      {error ? (
        <ErrorState error={error} />
      ) : loading || !data ? (
        <div style={{ display: "grid", gap: 10 }}>
          <Skeleton width="30%" />
          <Skeleton height={80} />
          <Skeleton height={80} />
        </div>
      ) : (
        <>
          <Badge tone={style.tone} icon={style.icon}>
            {titleCase(data.category)}
          </Badge>
          <ResourceDescription text={data.description} />
        </>
      )}
    </Modal>
  );
}

export default function Resources() {
  const { page, size, get, set } = usePaging(24);
  const category = get("category");
  const search = get("search");
  const openId = get("open");

  const { data, error, loading, reload } = useApi("/resources", { category, search, page, size });
  const dashboard = useApi("/dashboard");
  const categories = Object.entries(dashboard.data?.resourcesByCategory || {});
  const allCount = categories.reduce((sum, [, count]) => sum + count, 0);

  return (
    <>
      <PageHeader
        icon={BookOpen}
        title="Resources"
        subtitle="The university knowledge base the voice agent answers questions from"
      />

      <div className="resource-toolbar">
        <div className="chips">
          <button className={`chip ${!category ? "is-active" : ""}`} onClick={() => set({ category: null, page: null })}>
            All {allCount > 0 && <span>{allCount}</span>}
          </button>
          {categories.map(([name, count]) => {
            const style = categoryStyle(name);
            return (
              <button
                key={name}
                className={`chip ${category?.toUpperCase() === name ? "is-active" : ""}`}
                onClick={() => set({ category: name, page: null })}
              >
                <style.icon size={14} /> {titleCase(name)} <span>{count}</span>
              </button>
            );
          })}
        </div>
        <SearchInput value={search} placeholder="Search titles" onChange={(value) => set({ search: value, page: null })} />
      </div>

      {error ? (
        <ErrorState error={error} onRetry={reload} />
      ) : loading && !data ? (
        <div className="resource-grid">
          {Array.from({ length: 6 }).map((_, index) => (
            <div className="resource-card" key={index}>
              <Skeleton width="35%" />
              <Skeleton width="80%" height={18} />
              <Skeleton height={48} />
            </div>
          ))}
        </div>
      ) : data.items.length === 0 ? (
        <EmptyState
          icon={BookOpen}
          title="No resources found"
          message={search || category ? "Try another category or search term." : "The knowledge base is empty."}
        />
      ) : (
        <>
          <div className={`resource-grid ${loading ? "is-refreshing" : ""}`}>
            {data.items.map((resource) => {
              const style = categoryStyle(resource.category);
              return (
                <button key={resource.id} className="resource-card" onClick={() => set({ open: resource.id })}>
                  <span className={`resource-card__icon tone-${style.tone}`}>
                    <style.icon size={18} />
                  </span>
                  <Badge tone={style.tone}>{titleCase(resource.category)}</Badge>
                  <h3>{resource.title}</h3>
                  <p className="clamp-3">{resource.description}</p>
                  <span className="resource-card__more">Read more →</span>
                </button>
              );
            })}
          </div>
          <div className="card card--flat">
            <Pagination
              page={data.page}
              size={data.size}
              total={data.total}
              totalPages={data.totalPages}
              onPageChange={(next) => set({ page: next })}
            />
          </div>
        </>
      )}

      <ResourceModal id={openId} onClose={() => set({ open: null })} />
    </>
  );
}
