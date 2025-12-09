## 📌 AI Document Summarizer

A **full-stack system** designed to efficiently process and summarize PDF/TXT documents using AI. It provides **section-wise summaries** and a **complete, concise document overview**, with export options for easy sharing and integration.

-----

### ✨ Features & Functionality

| Feature | Status | Description |
| :--- | :--- | :--- |
| **PDF/TXT Upload** | ✅ | Accepts standard document formats for processing. |
| **Text Extraction** | **PDFBox + Tika** | Uses robust Apache libraries for accurate text extraction from uploaded files. |
| **Chunk Splitting** | **Smart 1800-char** | Splits extracted text into manageable chunks (approx. 1800 characters) for efficient AI processing. |
| **AI Summaries** | Per-chunk + combined | Generates individual summaries for each text chunk and a final **overall document summary**. |
| **Export Options** | Yes | Supports **Copy to Clipboard**, **Download Summary** (TXT), and **Download JSON** export. |
| **Rate Limit Recovery** | Graceful retry | Implements logic to handle and recover from OpenAI rate limit errors (429). |

-----

### 🏗️ Tech Stack

| Layer | Technology |
| :--- | :--- |
| **Frontend** | React + Axios |
| **Backend** | **Spring Boot (Java 17)** |
| **Database** | **PostgreSQL** |
| **AI Model** | **OpenAI gpt-4o-mini** via `/v1/responses` |
| **File Parsing** | Apache Tika, Apache PDFBox |
| **Environment** | `.env` variables |

-----

### 🌐 System Design Overview

The system follows a typical microservice pattern where the Frontend communicates with the Spring Boot Backend, which orchestrates document parsing, AI summarization, and persistence in the PostgreSQL database.

-----

### 📂 Project Structure Highlights

The project is logically divided into `backend` (Spring Boot) and `frontend` (React) components:

  * **`backend/`**:
      * **`DocumentController.java`**: Handles API requests (upload, get summaries).
      * **`DocumentService.java`**: Core business logic (chunking, calling OpenAI, saving to DB).
      * **`OpenAIClient.java`**: Dedicated service for communicating with the OpenAI API.
      * **`Document.java`**: JPA entity for database persistence.
  * **`frontend/`**:
      * **`Upload.js`**: Component for handling file uploads.
      * **`SummaryDisplay.js`**: Component for rendering and exporting generated summaries.

-----

### 🚀 Setup and Local Deployment

Follow these steps to get the application running locally:

#### 1️⃣ Clone Repository

```bash
git clone https://github.com/yourusername/ai-document-summarizer.git
cd ai-document-summarizer
```

#### 2️⃣ Environment Variables

Create a file named **`.env`** in the **`backend`** root directory:

```bash
OPENAI_API_KEY=sk-...
OPENAI_API_URL=https://api.openai.com/v1/responses
OPENAI_MODEL=gpt-4o-mini
```

#### 3️⃣ Database Setup (PostgreSQL)

Set up the database and configure the Spring Boot application:

1.  **Start PostgreSQL** and create the database:
    ```bash
    psql -U postgres
    CREATE DATABASE summarydb;
    ```
2.  **Configure DB in `backend/src/main/resources/application.properties`**:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/summarydb
    spring.datasource.username=postgres
    spring.datasource.password=your-password
    spring.jpa.hibernate.ddl-auto=update
    ```

#### 4️⃣ Backend Setup (Spring Boot)

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

🚪 App Runs At: **`http://localhost:8080`**

#### 5️⃣ Frontend Setup (React)

```bash
cd frontend
npm install
npm start
```

Runs At: **`http://localhost:3000`**

-----

### 📈 Chunking Logic

The application uses a simple, yet effective, character-based chunking logic to segment the document's text before sending it to the AI model. This is crucial for managing token limits and optimizing summary quality.

```java
// Logic Highlight from DocumentService
while (start < cleaned.length()) {
    chunks.add(cleaned.substring(start, Math.min(start + maxLen, cleaned.length())));
    start += maxLen;
}
```

  * **`maxLen`** is approximately **1800 characters**.
  * This ensures all parts of the document are processed sequentially.

-----

### 📸 Architecure Diagram and Sytem Design
<img width="1024" height="1024" alt="image" src="https://github.com/user-attachments/assets/d05dcc9c-be3d-4dbb-8e56-9e19f11be548" />

-----

### 🛠️ Troubleshooting Guide

| Issue | Fix |
| :--- | :--- |
| **429 rate\_limit\_exceeded** | Wait 20–60 seconds, or review your OpenAI usage plan. |
| **Summaries showing only in JSON** | Restart the application and consider lowering the concurrent chunk processing threads (if configured). |
| **`.env` not loading** | Ensure the `.env` file is in the **backend root** and restart your IDE/Spring app. |
| **Invalid API key** | Regenerate the key on the OpenAI platform and restart the Spring app. |
