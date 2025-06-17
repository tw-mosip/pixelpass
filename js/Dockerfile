# Use an official Node.js runtime as the base image
FROM node:20-alpine

WORKDIR /app

COPY package*.json ./

RUN npm install --omit=dev

COPY . .

WORKDIR /app/example

RUN npm install --omit=dev

EXPOSE 3000

CMD ["npm", "start"]