import { defineConfig } from 'vite'
import { svelte } from '@sveltejs/vite-plugin-svelte'

const ASSET_URL = process.env.ASSET_URL || '';

export default defineConfig(({ command, mode, ssrBuild }) => {
    if (command === 'serve') {
        return {
            plugins: [svelte()],
        }
        
    } else {
        // production build
        return {
            base: "retro",
            plugins: [svelte()],
        }
    }
});








