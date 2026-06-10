FROM node:20-alpine

WORKDIR /app
COPY server.js .

ENV PORT=9090
ENV MOCK_SMS_DELAY_MS=2000

EXPOSE 9090
CMD ["node", "server.js"]
