import type { Message } from '../types/message';

const getSortKey = (message: Message) => {
  const parsed = Date.parse(message.createdAt);
  if (!Number.isNaN(parsed)) {
    return parsed;
  }
  return message.tempId ?? 0;
};

export const mergeMessages = (
  serverMessages: Message[],
  persistedMessages: Message[],
) => {
  const serverTempIds = new Set(
    serverMessages
      .map((msg) => msg.tempId)
      .filter((tempId): tempId is number => tempId !== null),
  );
  const serverChatMessageIds = new Set(
    serverMessages
      .map((msg) => msg.chatMessageId)
      .filter((chatMessageId): chatMessageId is number => !!chatMessageId),
  );
  const serverMessageIds = new Set(
    serverMessages
      .map((msg) => msg.messageId)
      .filter((messageId): messageId is string => !!messageId),
  );

  const filteredPersisted = persistedMessages.filter((msg) => {
    if (msg.chatMessageId && serverChatMessageIds.has(msg.chatMessageId)) {
      return false;
    }
    if (msg.messageId && serverMessageIds.has(msg.messageId)) {
      return false;
    }
    if (msg.tempId !== null && serverTempIds.has(msg.tempId)) {
      return false;
    }
    return true;
  });

  const merged = [...serverMessages, ...filteredPersisted];

  return merged.sort((a, b) => getSortKey(a) - getSortKey(b));
};
