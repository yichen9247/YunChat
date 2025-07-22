/* eslint-disable no-unused-vars */
/**
 * @file Vite configuration file
 * @description Configures build, plugins, and other settings for the Vue.js application
 * 
 * Key features:
 * - Auto imports Vue components and Element Plus
 * - Base64 encoding for small images
 * - Code splitting and chunk optimization
 * - Gzip compression
 * - Alias configuration for src directory
 */

/* eslint-disable no-undef */

import fs from "fs"
import path from 'path'
import vue from '@vitejs/plugin-vue'
import { defineConfig, loadEnv } from 'vite'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import viteCompression from 'vite-plugin-compression'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

// Plugin to convert small images to base64
const base64SetPlugin = async (limit = 4096) => {
    return {
        name: "base64-set-plugin",
        async transform(_, id) {
            if (!/\.(png|jpg|svg)$/.test(id)) return

            const stat = await fs.promises.stat(id)
            if (stat.size > limit) return

            const buffer = await fs.promises.readFile(id)
            const base64 = buffer.toString("base64")
            const dataUrl = `data:image/${id.split(".").pop()};base64,${base64}`

            return {
                code: `export default ${JSON.stringify(dataUrl)}`
            }
        }
    }
}

export default ({ command, mode }) => {

    const envList = [
        loadEnv(mode, process.cwd()).VITE_SERVER_IP,
        loadEnv(mode, process.cwd()).VITE_SERVER_URL,
        loadEnv(mode, process.cwd()).VITE_APP_SCAN_LOGIN,
        loadEnv(mode, process.cwd()).VITE_APP_SECRET_KEY,
        loadEnv(mode, process.cwd()).VITE_APP_AUTO_SHOWIMAGE
    ];

    for (const element of envList) {
        if (element === undefined) {
            console.log(`\n\u001b[31m请检查服务配置信息后重试！\u001b[0m\n`);
            return process.exit(0);
        }
    }

    return defineConfig({
        define: {
            'process.env': process.env,
            __VUE_PROD_HYDRATION_MISMATCH_DETAILS__: 'true',
        },

        build: {
            rollupOptions: {
                treeshake: true,
                output: {
                    manualChunks(id) {
                        if (id.includes('node_modules')) {
                            return id.toString().split('node_modules/')[1].split('/')[0].toString()
                        }
                    },
                    chunkFileNames: 'static/js/[name]-[hash].js',
                    entryFileNames: 'static/js/[name]-[hash].js',
                    assetFileNames: 'static/[ext]/[name]-[hash].[ext]',
                    commonjsOptions: {
                        requireReturnsDefault: 'namespace'
                    }
                },
                chunkSizeWarningLimit: 1500
            },
            brotliSize: false,
            assetsDir: 'assets',
            assetsInlineLimit: 5 * 1024 * 1024,
        },

        server: { host: '0.0.0.0' },

        plugins: [
            base64SetPlugin(),
            AutoImport({
                imports: ['vue'],
                resolvers: [ElementPlusResolver()]
            }),
            Components({
                resolvers: [ElementPlusResolver()],
                dirs: ['src'],
                extensions: ['vue', 'jsx'],
                dts: 'YunChat.d.ts',
            }),
            vue({
                template: {
                    compilerOptions: {
                        isCustomElement: (tag) => tag.startsWith('mdui-')
                    }
                }
            }),
            viteCompression({
                verbose: true,
                disable: false,
                deleteOriginFile: false,
                threshold: 5120,
                algorithm: 'gzip',
                ext: '.gz'
            }),
        ],

        css: {
            modules: { generateScopedName: '[name]-[hash:base64:5]' }
        },

        resolve: {
            alias: {
                '@': path.resolve(__dirname, './src'),
            },
            entry: '/src/application.js',
            output: {
                manualChunks(id) {
                    if (id.includes('node_modules')) return 'vendor'
                }
            }
        }
    });
};