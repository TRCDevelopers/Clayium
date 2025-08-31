import {defineConfig} from "vitepress";

export const shared = defineConfig({
    title: "Clayium Wiki",
    description: "a wiki for Clayium Unofficial",
    base: "/Clayium/",
    lastUpdated: false,
    vite: {
        plugins: [],
    },
    markdown: {
        math: true,
    },
    themeConfig: {
        socialLinks: [
            { icon: "github", link: "https://github.com/TRCDevelopers/Clayium" }
        ]
    }
})