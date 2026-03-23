/* ==============================
   TẠO CƠ SỞ DỮ LIỆU
   ============================== */
CREATE DATABASE ChatOnlineDB;
GO

USE ChatOnlineDB;
GO

/* ==============================
   BẢNG USERS
   Lưu thông tin người dùng
   ============================== */
CREATE TABLE Users (
    UserID INT IDENTITY(1,1) PRIMARY KEY,
    Username NVARCHAR(50) NOT NULL UNIQUE,
    Password NVARCHAR(255) NOT NULL,
    Email NVARCHAR(100),
    CreatedAt DATETIME DEFAULT GETDATE()
);
GO

/* ==============================
   BẢNG CONVERSATIONS
   Lưu thông tin cuộc trò chuyện
   ============================== */
CREATE TABLE Conversations (
    ConversationID INT IDENTITY(1,1) PRIMARY KEY,
    CreatedAt DATETIME DEFAULT GETDATE()
);
GO

/* ==============================
   BẢNG MESSAGES
   Lưu nội dung tin nhắn
   ============================== */
CREATE TABLE Messages (
    MessageID INT IDENTITY(1,1) PRIMARY KEY,
    ConversationID INT NOT NULL,
    SenderID INT NOT NULL,
    Content NVARCHAR(MAX) NOT NULL,
    SentAt DATETIME DEFAULT GETDATE(),
    IsRead BIT DEFAULT 0,

    CONSTRAINT FK_Messages_Conversations
        FOREIGN KEY (ConversationID)
        REFERENCES Conversations(ConversationID),

    CONSTRAINT FK_Messages_Users
        FOREIGN KEY (SenderID)
        REFERENCES Users(UserID)
);
GO
ALTER TABLE Users ADD Avatar NVARCHAR(255);