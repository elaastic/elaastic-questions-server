import type { Meta, StoryObj } from '@storybook/vue3'
import ElStatement from '@/components/statement/ElStatement.vue'
import {ref} from "vue";
const meta = {
  title: 'statement/ElStatement',
  component: ElStatement,
  tags: ['autodocs'],
} satisfies Meta<typeof ElStatement>

export default meta
type Story = StoryObj<typeof meta>

export const Default: Story = {
  render: (args) =>({
    components: {ElStatement},
    setup(){
      const isOpen = ref(args.panelOpen);
      return {
        args, isOpen
      }
    },
    template: `
      <ElStatement v-bind="args" :panel-open="isOpen">
        <h4>Statement\'s content</h4>
        <ul>
          <li>First point</li>
          <li>Second point</li>
          <li>Third point</li>
        </ul>
      </ElStatement>
    `
  }),
  args: {
    title: 'Statement\'s title',
    questionType: "MultipleChoice",
    panelOpen: true,
    hideStatement: false,
    hideQuestionType: false
  },
}
export const PanelClosed: Story = {
  render: (args) => ({
    components: {ElStatement},
    setup(){
      const isOpen = ref(args.panelOpen);
      return{
        isOpen, args
      }
    },
    template: `
      <ElStatement v-bind="args" :panel-open="isOpen">
        <h4>Statement\'s content</h4>
        <ul>
          <li>First point</li>
          <li>Second point</li>
          <li>Third point</li>
        </ul>
      </ElStatement>
    `
  }),
  args: {
    title: 'Statement\'s title',
    questionType: "MultipleChoice",
    panelOpen: false,
    hideStatement: false,
    hideQuestionType: false
  },
}
export const HiddenQuestionType: Story = {
  render: (args) => ({
    components: {ElStatement},
    setup(){
      const isOpen = ref(args.panelOpen);
      return{
        isOpen, args
      }
    },
    template: `
      <ElStatement v-bind="args" :panel-open="isOpen">
        <h4>Statement\'s content</h4>
        <ul>
          <li>First point</li>
          <li>Second point</li>
          <li>Third point</li>
        </ul>
      </ElStatement>
    `
  }),
  args: {
    title: 'Statement\'s title',
    questionType: "MultipleChoice",
    panelOpen: true,
    hideStatement: false,
    hideQuestionType: true
  },
}
export const HiddenStatement: Story = {
  render: (args) => ({
    components: {ElStatement},
    setup(){
      const isOpen = ref(args.panelOpen);
      return{
        isOpen, args
      }
    },
    template: `
      <ElStatement v-bind="args" :panel-open="isOpen">
        <h4>Statement\'s content</h4>
        <ul>
          <li>First point</li>
          <li>Second point</li>
          <li>Third point</li>
        </ul>
      </ElStatement>
    `
  }),
  args: {
    title: 'Statement\'s title',
    questionType: "MultipleChoice",
    panelOpen: true,
    hideStatement: true,
    hideQuestionType: false
  },
}




