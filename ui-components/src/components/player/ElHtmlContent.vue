<!--
This component defines the CSS style for presenting HTML content within
the Elaastic player
-->
<script setup lang="ts"></script>

<template>
  <div class="content-html">
    <slot />
  </div>
</template>

<style scoped>
.content-html :deep(ul) {
  padding-left: 4rem;
  margin: 1rem 0;
}

.content-html :deep(ol) {
  padding-left: 4rem;
  margin: 1rem 0;
}
</style>
