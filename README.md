📌 AI Document Summarizer

A full-stack system that uploads PDF/TXT documents, extracts text, chunks content smartly, and generates:

✔️ Section-wise summary
✔️ Complete concise document overview
✔️ Copy / JSON export / summary export

🏗️ Tech Stack
Layer	Technology
Frontend	React + Axios
Backend	Spring Boot (Java 17)
Database	PostgreSQL
AI Model	OpenAI gpt-4o-mini via /v1/responses
File Parsing	Apache Tika, Apache PDFBox
Environment	.env variables
🌐 Architecture Diagram
System Design Overview
<img src="./architecture.png" width="650">

OR if embedding full resolution:

/docs/architecture.png

📂 Project Structure
ai-document-summarizer/
│
├── backend/
│   ├── src/main/java/com/summarizer/controller/DocumentController.java
│   ├── src/main/java/com/summarizer/service/DocumentService.java
│   ├── src/main/java/com/summarizer/service/OpenAIClient.java
│   ├── src/main/java/com/summarizer/model/Document.java
│   ├── src/main/java/com/summarizer/repository/DocumentRepository.java
│   └── src/main/resources/application.properties
│
├── frontend/
│   ├── src/components/Upload.js
│   ├── src/components/SummaryDisplay.js
│   ├── src/styles/upload-ui.css
│   ├── src/styles/summary-ui.css
│   └── package.json
│
└── README.md

🚀 Setup Instructions
1️⃣ Clone Repository
git clone https://github.com/yourusername/ai-document-summarizer.git
cd ai-document-summarizer

2️⃣ Environment Variables

Create .env in backend root:

OPENAI_API_KEY=sk-...
OPENAI_API_URL=https://api.openai.com/v1/responses
OPENAI_MODEL=gpt-4o-mini


Backend also keeps:

src/main/resources/application.properties

3️⃣ Backend Setup (Spring Boot)
cd backend
mvn clean install
mvn spring-boot:run

🚪 App Runs At:
http://localhost:8080

4️⃣ Database Setup (PostgreSQL)
psql -U postgres
CREATE DATABASE summarydb;


application.properties:

spring.datasource.url=jdbc:postgresql://localhost:5432/summarydb
spring.datasource.username=postgres
spring.datasource.password=your-password
spring.jpa.hibernate.ddl-auto=update

5️⃣ Frontend Setup (React)
cd frontend
npm install
npm start


Runs at:

http://localhost:3000

🎯 Features
Feature	Status
PDF/TXT Upload	✅
Text Extraction	PDFBox + Tika
Chunk Splitting	Smart 1800-char
AI Summaries	Per-chunk + combined
Copy to Clipboard	Yes
Download Summary	Yes
Download JSON	Yes
Rate Limit Recovery	Graceful retry
.env secured keys	Yes
📸 UI Screenshots
Upload & Summary Dashboard
<img src="./docs/ui-summary.png" width="800">
🧪 Demo Recording Requirements

Your submission video must cover:

✔️ What problem is solved
✔️ Upload a document live
✔️ Show Section Summary + Overall Summary
✔️ JSON & Copy export
✔️ Database entry proof

📈 Chunking Logic Highlight
while (start < cleaned.length()) {
    chunks.add(cleaned.substring(start, Math.min(start + maxLen, cleaned.length())));
    start += maxLen;
}

🛠️ Troubleshooting
Issue	Fix
429 rate_limit_exceeded	Wait 20–60 seconds (or add card to OpenAI)
Summaries showing only in JSON	restart + lower chunk threads
.env not loading	restart IDE, ensure at backend root
Invalid API key	regenerate + restart Spring app

