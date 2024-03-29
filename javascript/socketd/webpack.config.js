const path = require('path');

module.exports = {
    entry: './dist/build/SocketD.js',
    mode: 'production', //'none' | 'development' | 'production'
    experiments: {
        outputModule: true,
    },
    output: {
        // 把所有依赖的模块合并输出到一个 bundle.js 文件
        filename: 'socket.d.js',
        // 输出文件都放到 dist 目录下
        path: path.resolve(__dirname, './dist/release'),
        libraryTarget: 'window'//'commonjs2'
    }
}
