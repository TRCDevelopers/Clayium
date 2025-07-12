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
    },
    head: [
        [
            'link',
            { rel: 'preconnect', href: 'https://fonts.googleapis.com' }
        ],
        [
            'link',
            { rel: 'preconnect', href: 'https://fonts.gstatic.com', crossorigin: '' }
        ],
        [
            'link',
            { href: 'https://fonts.googleapis.com/css2?family=Noto+Sans+JP:wght@100..900&display=swap', rel: 'stylesheet' }
        ]
    ]
})