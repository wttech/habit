/* eslint-disable */
const path = require("path");
const webpack = require("webpack");

const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin;

require('dotenv').config();

const isDevelopment = process.env.NODE_ENV === 'development';

const API_URL = process.env.HABIT_API_URL || 'http://localhost:7080';

module.exports = {
    entry: "./src/js/index.ts",
    module: {
        rules: [
            {
                test: /\.(js|jsx)$/,
                exclude: /(node_modules|bower_components)/,
                use: {
                    loader: require.resolve('babel-loader'),
                    options: { presets: ["@babel/env"] }
                },
            },
            {
                test: /\.(ts|tsx)$/,
                exclude: /(node_modules|bower_components)/,
                use: "ts-loader"
            },
            {
                test: /\.s(a|c)ss$/,
                exclude: /\.module.(s(a|c)ss)$/,
                use: [
                    MiniCssExtractPlugin.loader,
                    'css-loader',
                    {
                        loader: 'sass-loader',
                        options: {
                            sourceMap: isDevelopment,
                        }
                    }
                ]
            },
            {
                test: /\.png/,
                type: 'asset/resource',
                generator: {
                    publicPath: '/resources/'
                }
            },
        ]
    },
    resolve: { 
        extensions: [".js", ".jsx", ".ts", ".tsx", ".scss"],
        alias: {
            '@': path.join(__dirname, 'src', 'js')
        },
    },
    output: {
        path: path.resolve(__dirname, "build"),
        publicPath: "/",
        filename: "bundle.js"
    },
    devServer: {
        port: 3000,
        historyApiFallback: {
            index: '/'
        },
        proxy: {
            "/api": API_URL
        }
    },
    plugins: [
        new webpack.DefinePlugin({
            "process.env": JSON.stringify(process.env)
        }),
        new MiniCssExtractPlugin({
            filename: '[name].css',
            chunkFilename: '[id].css'
        }),
        new HtmlWebpackPlugin({
            title: 'Index generator',
            template: 'src/index.html',
            publicPath: 'resources/'
        }),
        // npm run bundle-report to check the analysis
        new BundleAnalyzerPlugin({
            analyzerMode: 'disabled',
            generateStatsFile: true,
            statsOptions: { source: false }
        }),
    ]
};
