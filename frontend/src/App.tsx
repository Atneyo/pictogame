import { Route, Routes } from "react-router";
import Layout from "./components/Layout.tsx";
import DictationPage from "./pages/DictationPage.tsx";
import HomePage from "./pages/HomePage.tsx";
import NotFoundPage from "./pages/NotFoundPage.tsx";

function App() {
  return (
    <Routes>
      {/* Every page below is rendered inside the Layout (header + content area) */}
      <Route element={<Layout />}>
        <Route path="/" element={<HomePage />} />
        <Route path="/dictation" element={<DictationPage />} />
        {/* Catch-all: any unknown URL */}
        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
  );
}

export default App;
