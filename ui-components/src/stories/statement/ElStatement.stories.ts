import type { Meta, StoryObj } from '@storybook/vue3'
import ElStatement from '@/components/statement/ElStatement.vue'
const meta = {
  title: 'statement/ElStatement',
  component: ElStatement,
  tags: ['autodocs'],
} satisfies Meta<typeof ElStatement>

export default meta
type Story = StoryObj<typeof meta>

export const Default: Story = {
  args: {
    title: 'Statement\'s title',
    contentStatement: "<h4>Statement\'s content</h4>" +
            "<ul>" +
            "<li>First point</li>" +
            "<li>Second point</li>" +
            "<li>Third point</li>" +
            "</ul>",
    questionType: "MultipleChoice",
    panelOpen: true,
    hideStatement: false,
    check1: false,
    check2: false,
    check3: false
  },
}
export const PanelClosed: Story = {
  args: {
    title: 'Statement\'s title',
    contentStatement: "<h4>Statement\'s content</h4>" +
            "<ul>" +
            "<li>First point</li>" +
            "<li>Second point</li>" +
            "<li>Third point</li>" +
            "</ul>",
    questionType: "MultipleChoice",
    panelOpen: true,
    hideStatement: false,
    check1: true,
    check2: false,
    check3: false
  },
}
export const HiddenQuestionType: Story = {
  args: {
    title: 'Statement\'s title',
    contentStatement: "<h4>Statement\'s content</h4>" +
            "<ul>" +
            "<li>First point</li>" +
            "<li>Second point</li>" +
            "<li>Third point</li>" +
            "</ul>",
    questionType: "MultipleChoice",
    panelOpen: true,
    hideStatement: false,
    check1: false,
    check2: true,
    check3: false
  },
}
export const HiddenStatement: Story = {
  args: {
    title: 'Statement\'s title',
    contentStatement: "<h4>Statement\'s content</h4>" +
            "<ul>" +
            "<li>First point</li>" +
            "<li>Second point</li>" +
            "<li>Third point</li>" +
            "</ul>",
    questionType: "MultipleChoice",
    panelOpen: true,
    hideStatement: false,
    check1: false,
    check2: false,
    check3: true
  },
}




