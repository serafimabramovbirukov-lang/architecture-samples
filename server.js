const http = require('http');
const { Server } = require('socket.io');

const server = http.createServer();
const io = new Server(server, {
  cors: { origin: "*" } // Разрешаем подключения с любых устройств
});

io.on('connection', (socket) => {
  console.log('Пользователь подключился:', socket.id);

  socket.on('chat message', (msg) => {
    console.log('Сообщение:', msg);
    io.emit('chat message', msg); // Отправляем всем
  });

  socket.on('disconnect', () => {
    console.log('Пользователь отключился');
  });
});

const PORT = process.env.PORT || 3000;
server.listen(PORT, () => {
  console.log(`Сервер запущен на порту ${PORT}`);
});
