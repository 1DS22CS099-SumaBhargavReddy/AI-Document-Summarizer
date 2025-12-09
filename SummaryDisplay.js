import React from "react";
import "./summary-ui.css";

const SummaryDisplay = ({ data }) => {
  if (!data) return null;

  const copyText = () => navigator.clipboard.writeText(data.overallSummary);

  const downloadFile = (type) => {
    const element = document.createElement("a");
    const file = new Blob(
      [type === "full" ? JSON.stringify(data, null, 2) : data.overallSummary],
      { type: "text/plain" }
    );
    element.href = URL.createObjectURL(file);
    element.download = type === "full" ? "full-summary.json" : "summary.txt";
    document.body.appendChild(element);
    element.click();
  };

  return (
    <div className="summary-card slide-up">
      <h2>📝 Section-wise Summary</h2>

      {data.sectionSummaries?.map((s, i) => (
        <div key={i} className="summary-block fade-in">
          <strong>Section {i + 1}</strong>
          <p>{s}</p>
        </div>
      ))}

      <h2>📌 Overall Document Gist</h2>
      <div className="overall-box fade-in">{data.overallSummary}</div>

      <div className="action-row">
        <button className="copy-btn" onClick={copyText}>📋 Copy</button>
        <button className="download-btn" onClick={() => downloadFile("summary")}>
          ⬇ Download Summary
        </button>
        <button className="download-btn" onClick={() => downloadFile("full")}>
          ⬇ Download Full JSON
        </button>
      </div>
    </div>
  );
};

export default SummaryDisplay;
