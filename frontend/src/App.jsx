import { Route, Routes } from "react-router-dom";
import Layout from "./components/Layout";
import Dashboard from "./pages/Dashboard";
import Leads from "./pages/Leads";
import LeadDetail from "./pages/LeadDetail";
import Chats from "./pages/Chats";
import { Exams, Feedbacks, Grades, Notes, Schedules } from "./pages/Records";
import Resources from "./pages/Resources";
import Admin from "./pages/Admin";
import NotFound from "./pages/NotFound";

export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<Dashboard />} />
        <Route path="leads" element={<Leads />} />
        <Route path="leads/:id" element={<LeadDetail />} />
        <Route path="chats" element={<Chats />} />
        <Route path="feedbacks" element={<Feedbacks />} />
        <Route path="notes" element={<Notes />} />
        <Route path="schedules" element={<Schedules />} />
        <Route path="exams" element={<Exams />} />
        <Route path="grades" element={<Grades />} />
        <Route path="resources" element={<Resources />} />
        <Route path="admin" element={<Admin />} />
        <Route path="*" element={<NotFound />} />
      </Route>
    </Routes>
  );
}
