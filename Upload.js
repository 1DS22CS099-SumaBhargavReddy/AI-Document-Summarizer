import React, { useState } from "react";
import axios from "axios";
import "./upload-ui.css";

const Upload = ({ onSummary }) => {
  const [file, setFile] = useState(null);
  const [mode, setMode] = useState("medium");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleUpload = async () => {
    if (!file) {
      setError("⚠ Please select a file first.");
      return;
    }

    setLoading(true);
    setError("");

    try {
      const formData = new FormData();
      formData.append("file", file);
      formData.append("mode", mode);

      const res = await axios.post(
        "http://localhost:8080/api/documents/upload",
        formData,
        {
          headers: { "Content-Type": "multipart/form-data" },
        }
      );

      onSummary && onSummary(res.data);
    } catch (err) {
      setError(err.response?.data || err.message || "Unknown summarizing error.");
      onSummary && onSummary(null);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="upload-card">
      <h2>📄 Upload Document</h2>

      <input
        type="file"
        accept=".pdf,.txt"
        onChange={(e) => {
          setFile(e.target.files[0] || null);
          setError("");
        }}
        className="file-input"
      />

      <select
        className="mode-select"
        value={mode}
        onChange={(e) => setMode(e.target.value)}
      >
        <option value="brief">⚡ Brief (3 bullets)</option>
        <option value="medium">📌 Medium (5–7 bullets)</option>
        <option value="detailed">📝 Detailed (paragraphs)</option>
      </select>

      <button className="upload-btn" onClick={handleUpload} disabled={loading}>
        {loading ? "Summarizing…" : "Upload & Summarize"}
      </button>

      {error && <p className="error-msg">{error}</p>}
    </div>
  );
};

export default Upload;
