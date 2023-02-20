import { defineConfig } from 'vite'
import { svelte } from '@sveltejs/vite-plugin-svelte'

export default defineConfig(({ command, mode, ssrBuild }) => {
    if (command === 'serve') {
        return {
            plugins: [svelte()],
        }
        
    } else {
        // production build
        return {
            base: "/poker",
            plugins: [svelte()],
        }
    }
});








