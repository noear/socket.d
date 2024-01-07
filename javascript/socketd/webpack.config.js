const path = require('path');

module.exports = env => {

    return {
        // JavaScript 执行入口文件
        entry: './dist/build/SocketD.js',
        mode: 'production', //'none' | 'development' | 'production'
        //target: 'node', //'node' | 'web'
        experiments: {
            outputModule: true,
        },
        output: {
            // 把所有依赖的模块合并输出到一个 bundle.js 文件
            filename: env.OUT_FILE_NAME || 'socket.d.js',
            // 输出文件都放到 dist 目录下
            path: path.resolve(__dirname, './dist/release'),
            libraryTarget: env.LIB_TARGET || 'window'
        }
    };
}
