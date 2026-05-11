# 📁 Images Directory Structure

Please copy/save screenshots and diagrams to the appropriate folders below.

## Directory Structure

```
docs/images/
├── README.md                      # This file
├── architecture/                  # System architecture diagrams
├── ui-screenshots/                # UI/UX screenshots
├── chat-ui/                       # Chat interface screenshots
├── ocr-samples/                   # OCR demonstration images
└── voice-demo/                    # Voice input demonstration

```

---

## Architecture Diagrams

### `docs/images/architecture/`

Store system architecture and technical diagrams here:

```
architecture/
├── system-architecture.png          # Main system architecture diagram
├── data-flow-voice.png              # Voice input data flow
├── data-flow-ocr.png                # OCR data flow
├── data-flow-chat.png               # Chat AI data flow
├── database-schema.png              # Database schema diagram
├── deployment-architecture.png      # Deployment diagram (Docker/K8s)
├── component-interaction.png        # Component interaction diagram
├── api-gateway-flow.png             # API gateway flow
└── microservices-diagram.png        # Microservices architecture
```

**How to create:**
- Use tools like: **Lucidchart**, **Draw.io**, **Miro**, **PlantUML**
- Export as PNG (1200x800px recommended)
- Include all layers: Frontend, Backend, AI, Database

---

## UI Screenshots

### `docs/images/ui-screenshots/`

Store user interface screenshots here:

```
ui-screenshots/
├── 01-login-page.png               # Login screen
├── 02-register-page.png            # Registration screen
├── 03-dashboard-overview.png       # Main dashboard with balance
├── 04-wallet-list.png              # List of wallets
├── 05-wallet-detail.png            # Individual wallet detail
├── 06-transactions-list.png        # Transaction history list
├── 07-transaction-detail.png       # Individual transaction detail
├── 08-transaction-form.png         # Form to add/edit transaction
├── 09-transaction-categories.png   # Category selection
├── 10-recent-transactions.png      # Recent transactions widget
├── 11-balance-card.png             # Balance display card
├── 12-reports-page.png             # Reports/analytics page
├── 13-reports-by-category.png      # Category breakdown chart
├── 14-reports-timeline.png         # Expense timeline
├── 15-settings-page.png            # User settings
├── 16-profile-page.png             # User profile
└── 17-responsive-mobile.png        # Mobile responsive view
```

**How to capture:**
- Use Chrome DevTools (F12)
- Mobile view: 375x812px (iPhone)
- Desktop view: 1920x1080px
- Include annotations if needed

---

## Chat UI Screenshots

### `docs/images/chat-ui/`

Store chat interface and conversation examples:

```
chat-ui/
├── 01-chat-interface.png           # Chat UI layout
├── 02-chat-input-area.png          # Message input component
├── 03-message-bubble-user.png      # User message bubble
├── 04-message-bubble-ai.png        # AI response bubble
├── 05-chat-example-expense.png     # Example: Add expense
│  Message: "Ăn sáng 50k"
│  AI: "Ghi nhận chi tiêu ăn sáng 50k..."
├── 06-chat-example-income.png      # Example: Add income
│  Message: "Nhận lương 10 triệu"
│  AI: "Ghi nhận thu nhập lương 10 triệu..."
├── 07-chat-example-missing-info.png # Example: Missing info
│  Message: "Ăn gì à?"
│  AI: "Bạn ơi, bạn mua gì hôm nay?"
├── 08-chat-confirmation.png        # Confirm transaction
├── 09-chat-edit-interface.png      # Edit/modify transaction
├── 10-chat-category-select.png     # Category selection in chat
├── 11-chat-wallet-select.png       # Wallet selection in chat
├── 12-chat-history.png             # Chat conversation history
├── 13-chat-message-error.png       # Error handling in chat
└── 14-quick-reply-buttons.png      # Quick reply button options
```

**Conversation Examples to Capture:**

1. **Simple Expense**
   - User: "Ăn sáng 50 nghìn"
   - AI: "Ghi nhận chi tiêu: ăn sáng 50k. Lưu vào ví nào?"

2. **Multiple Items**
   - User: "Ăn sáng 50k, cà phê 20k"
   - AI: "Ghi nhận: ăn sáng + cà phê = 70k. Đúng không?"

3. **With Wallet**
   - User: "Thêm 100k vào ví tiền lương"
   - AI: "Ghi nhận: thêm 100k vào ví tiền lương"

4. **Query**
   - User: "Mình còn bao nhiêu trong ví?"
   - AI: "Ví tiền mặt của bạn còn 5 triệu"

5. **Correction**
   - User: "Sửa lại, mình chi 80k"
   - AI: "Được, sửa lại thành 80k. Bạn muốn lưu?"

---

## OCR Screenshots

### `docs/images/ocr-samples/`

Store OCR-related screenshots and example images:

```
ocr-samples/
├── 01-scanner-button.png           # Camera scanner button in UI
├── 02-camera-interface.png         # Camera capture interface
├── 03-invoice-preview.png          # Preview before scan
├── 04-scanning-process.png         # Scanning in progress
├── 05-example-receipt-1.jpg        # Example receipt image 1
│  └ Contains: Items, prices, total
├── 06-example-receipt-2.jpg        # Example receipt image 2
│  └ Contains: Restaurant bill
├── 07-example-invoice-1.jpg        # Example invoice 1
│  └ Contains: Electronics purchase
├── 08-ocr-extraction-result.png    # Extracted data display
│  Items:
│  - Cơm trắng: 30,000 VND
│  - Canh chua: 20,000 VND
│  - Nước cam: 15,000 VND
│  Total: 65,000 VND
├── 09-ocr-edit-interface.png       # Edit extracted data
├── 10-ocr-category-mapping.png     # Category auto-assignment
├── 11-ocr-amount-validation.png    # Validate amounts
├── 12-ocr-multiple-items.png       # Multiple items recognized
├── 13-ocr-error-handling.png       # OCR error/retry flow
├── 14-ocr-confirmation.png         # Final confirmation before save
└── 15-ocr-success-message.png      # Transactions saved message
```

**OCR Demo Ideas:**
- Restaurant receipt (Phở, Cơm Tấm)
- Coffee shop invoice
- Supermarket bill
- Gas station receipt
- Pharmacy invoice

---

## Voice Input Demonstrations

### `docs/images/voice-demo/`

Store voice input interface and flow screenshots:

```
voice-demo/
├── 01-voice-button.png             # Voice input button in UI
├── 02-recording-interface.png      # Recording UI with waveform
├── 03-recording-in-progress.png    # Active recording state
│  └ Timer: 0:03, Waveform animation
├── 04-recording-stopped.png        # Recording completed
├── 05-transcription-result.png     # Converted text display
│  "ăn sáng năm mươi nghìn, cà phê hai mươi nghìn"
├── 06-transcription-confidence.png # Confidence score display
├── 07-voice-classification.png     # AI classification result
│  Intent: add_expense
│  Amount: 70,000 VND
│  Category: Ăn uống
├── 08-voice-edit-interface.png     # Edit transcribed text
├── 09-voice-confirmation.png       # Confirm before save
├── 10-voice-success.png            # Transaction saved
├── 11-voice-error-handling.png     # Error/retry experience
├── 12-voice-ambient-noise.png      # Noise level indicator
├── 13-voice-quality-feedback.png   # Audio quality feedback
├── 14-voice-multiple-commands.png  # Multiple intent examples
└── 15-voice-quick-actions.png      # Quick action suggestions
```

**Voice Recording Examples:**

1. **Simple Transaction**
   - Audio: "Ăn sáng ba mươi nghìn"
   - Transcribed: "ăn sáng ba mươi nghìn"
   - Classified: EXPENSE, 30000 VND, Ăn uống

2. **Multiple Items**
   - Audio: "Ăn sáng ba mươi, cà phê hai mươi"
   - Transcribed: "ăn sáng ba mươi cà phê hai mươi"
   - Classified: EXPENSE, 50000 VND, Ăn uống

3. **Income**
   - Audio: "Nhận lương mười triệu"
   - Transcribed: "nhận lương mười triệu"
   - Classified: INCOME, 10000000 VND, Lương

4. **With Noise**
   - Original audio: (with background noise)
   - Processed: (noise reduced)
   - Transcribed: "..."

---

## Naming Conventions

### **Image Naming Format:**

```
[number]-[description].[format]

Examples:
✅ CORRECT:
  01-login-page.png
  02-dashboard-overview.png
  03-ocr-result.jpg
  04-voice-recording.gif

❌ INCORRECT:
  loginpage.png
  Screenshot 2025-05-09.png
  image.png
  temp_image.jpg
```

---

## Image Requirements

### **Resolution**
- **Diagrams**: 1200x800px minimum
- **Screenshots**: 750x1334px (mobile), 1920x1080px (desktop)
- **Infographics**: 1200x600px
- **Photos**: 1000x800px

### **Format**
- PNG: Diagrams, screenshots (lossless)
- JPG: Real photos (lossy)
- GIF: Animations, demos
- SVG: Vector diagrams (bonus)

### **Optimization**
- Max file size: 500KB per image
- Use image compression tools
- Maintain aspect ratio
- Use descriptive alt text

---

## Tools for Creating Images

### **Diagrams & Architecture**
- ✅ **Draw.io** (free, online)
- ✅ **Lucidchart** (professional)
- ✅ **Miro** (collaborative)
- ✅ **PlantUML** (code-based)
- ✅ **Excalidraw** (sketchy style)

### **Screenshots**
- ✅ **Chrome DevTools** (F12)
- ✅ **Snagit** (professional)
- ✅ **Screenshot Path** (macOS)
- ✅ **Print Screen** (Windows)

### **Image Editing**
- ✅ **Figma** (design tool)
- ✅ **Photoshop** (professional)
- ✅ **GIMP** (free alternative)
- ✅ **Canva** (easy design)

### **Compression**
- ✅ **TinyPNG** (online)
- ✅ **ImageOptim** (macOS)
- ✅ **FileOptimizer** (Windows)

---

## Integration in README

All images are referenced in the main [README.md](../README.md) with proper markdown links:

```markdown
## 🎨 Hình Ảnh & Demo

### **Kiến Trúc Hệ Thống**
![System Architecture](./docs/images/architecture/system-architecture.png)

### **Giao Diện Người Dùng**
![Dashboard](./docs/images/ui-screenshots/03-dashboard-overview.png)

### **Chat với AI**
![Chat UI](./docs/images/chat-ui/01-chat-interface.png)

### **OCR Hóa Đơn**
![OCR Result](./docs/images/ocr-samples/08-ocr-extraction-result.png)

### **Ghi Âm Giọng Nói**
![Voice Recording](./docs/images/voice-demo/03-recording-in-progress.png)
```

---

## Progress Checklist

Use this to track which images have been created:

### Architecture
- [ ] system-architecture.png
- [ ] data-flow-voice.png
- [ ] data-flow-ocr.png
- [ ] data-flow-chat.png
- [ ] database-schema.png
- [ ] deployment-architecture.png

### UI Screenshots
- [ ] login-page.png
- [ ] dashboard.png
- [ ] wallet-list.png
- [ ] transactions-list.png
- [ ] transaction-form.png
- [ ] reports-page.png

### Chat UI
- [ ] chat-interface.png
- [ ] chat-example-expense.png
- [ ] chat-example-income.png
- [ ] chat-confirmation.png

### OCR Samples
- [ ] camera-interface.png
- [ ] example-receipt-1.jpg
- [ ] ocr-extraction-result.png
- [ ] ocr-confirmation.png

### Voice Demo
- [ ] recording-interface.png
- [ ] transcription-result.png
- [ ] voice-classification.png
- [ ] voice-confirmation.png

---

**📝 Ready to add your images!**

