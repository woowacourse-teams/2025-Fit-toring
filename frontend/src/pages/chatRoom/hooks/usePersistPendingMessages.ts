import { useCallback, useEffect, useRef } from 'react';

import type { Message } from '../types/message';

const PERSIST_MESSAGE_KEY = 'persistMessage';

export const readPersistedMessages = (): Record<string, Message[]> => {
  try {
    const raw = localStorage.getItem(PERSIST_MESSAGE_KEY);
    if (!raw) {
      return {};
    }
    return JSON.parse(raw) as Record<string, Message[]>;
  } catch (error) {
    console.error('persistMessage read failed:', error);
    return {};
  }
};

const dedupeMessages = (messages: Message[]) => {
  const seen = new Set<string>();
  const result: Message[] = [];

  messages.forEach((msg) => {
    const key = String(msg.tempId);

    if (seen.has(key)) {
      return;
    }

    seen.add(key);
    result.push(msg);
  });

  return result;
};

const writePersistedMessages = (data: Record<string, Message[]>) => {
  try {
    const normalized: Record<string, Message[]> = {};

    Object.entries(data).forEach(([roomId, messages]) => {
      const dedupedMessages = dedupeMessages(messages ?? []);

      if (dedupedMessages.length > 0) {
        normalized[roomId] = dedupedMessages;
      }
    });

    localStorage.setItem(PERSIST_MESSAGE_KEY, JSON.stringify(normalized));
  } catch (error) {
    console.error('persistMessage write failed:', error);
  }
};

const usePersistPendingMessages = (
  chatRoomId: string | undefined,
  messages: Message[],
) => {
  const messagesRef = useRef<Message[]>(messages);

  useEffect(() => {
    messagesRef.current = messages;
  }, [messages]);

  const persistPendingMessages = useCallback(
    (overrideMessages?: Message[]) => {
      if (!chatRoomId) {
        return;
      }

      const sourceMessages = overrideMessages ?? messagesRef.current;
      const pendingOrFail = sourceMessages.filter(
        (msg) => msg.status === 'pending' || msg.status === 'fail',
      );
      if (pendingOrFail.length === 0) {
        return;
      }
      const persisted = readPersistedMessages();

      persisted[chatRoomId] = pendingOrFail;

      writePersistedMessages(persisted);
    },
    [chatRoomId],
  );

  useEffect(() => {
    const handleVisibilityChange = () => {
      if (document.visibilityState === 'hidden') {
        persistPendingMessages();
      }
    };

    const handlePageHide = () => {
      persistPendingMessages();
    };

    const handleBeforeUnload = () => {
      persistPendingMessages();
    };

    const handleOffline = () => {
      persistPendingMessages();
    };

    window.addEventListener('pagehide', handlePageHide);
    window.addEventListener('beforeunload', handleBeforeUnload);
    window.addEventListener('offline', handleOffline);
    document.addEventListener('visibilitychange', handleVisibilityChange);

    return () => {
      window.removeEventListener('pagehide', handlePageHide);
      window.removeEventListener('beforeunload', handleBeforeUnload);
      window.removeEventListener('offline', handleOffline);
      document.removeEventListener('visibilitychange', handleVisibilityChange);
    };
  }, [persistPendingMessages]);

  return persistPendingMessages;
};

export default usePersistPendingMessages;
