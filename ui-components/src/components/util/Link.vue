<script setup lang="ts">

export interface LinkProps {
  /**
   * The URL to link to
   */
  href: string;
  /**
   * The text to display for the link
   */
  text: string;
  /**
   * The target attribute for the link, defaults to "_self"
   */
  target?: string;
}
const props = withDefaults(defineProps<LinkProps>(), {
  target: "_self"
})

</script>

<template>
<a :href="props.href"
   :target="props.target"
   class="text-decoration-none text-primary">
  {{ props.text}}
</a>
</template>

<style scoped>

</style>
