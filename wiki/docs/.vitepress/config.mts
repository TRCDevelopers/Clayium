import {defineConfig} from 'vitepress'
import {ja} from "./config/ja"
import {en} from "./config/en"
import {shared} from "./config/shared";

// https://vitepress.dev/reference/site-config
export default defineConfig({
  ...shared,
  locales: {
    root: { label: "日本語", ...ja },
    en: { label: "English", ...en },
  },
})
