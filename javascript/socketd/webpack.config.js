const path = require('path');
module.exports = {
    mode: 'development',
    entry: './src/socketd.ts',//入口文件
    output: {
        filename: 'socketd.js',//编译出来的文件名
        path: path.resolve(__dirname, 'dist'),//编译文件所在目录
        publicPath: '/dist/',//静态资源目录,可以设为“编译文件所在目录”, 代码自动编译，网页自动刷新
    },
    module: {
        rules: [
            {
                // For our normal typescript
                test: /\.ts?$/,
                use: [
                    {
                        loader: 'ts-loader'
                    }
                ],
                exclude: /(?:node_modules)/,
            },
        ]
    },
    resolve: {
        modules: [
            'src',
            'node_modules'
        ],
        extensions: [
            '.js',
            '.ts'
        ]
    },
    devServer: {
        hot: true,
        compress: true,
        host: 'localhost',
        port: 8888
    },
    devtool:'source-map',
};