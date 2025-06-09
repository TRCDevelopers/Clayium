import {DefaultTheme, defineConfig} from 'vitepress'

export const en = defineConfig({
    lang: "en_US",
    themeConfig: {
        editLink: {
            pattern: "https://github.com/TRCDevelopers/Clayium/edit/develop/wiki/docs/:path",
            text: "Edit this page on GitHub",
        },
        sidebar: {
            "/en/": { base: "/en/", items: sidebar() }
        }
    }
})

function sidebar(): DefaultTheme.SidebarItem[] {
    return [
        {
            text: "Introduction",
            base: "/en/introduction/",
            collapsed: false,
            items: [
                { text: "Changes from original", link: "changes-from-original" },
            ],
        },
        {
            text: "Features",
            base: "/en/features/",
            collapsed: true,
            items: [
                { text: "Clay laser", link: "clay-laser" },
            ],
        },
        {
            text: "GroovyScript",
            base: "/en/groovy-script/",
            collapsed: true,
            items: [
                { text: "Recipe", link: "recipe" },
            ]
        },
    ]
}