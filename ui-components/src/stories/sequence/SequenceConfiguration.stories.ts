import type {Meta, StoryObj} from '@storybook/vue3-vite';
import SequenceConfiguration from "@/components/sequence/SequenceConfiguration.vue";
import {fn} from "storybook/test";


// More on how to set up stories at: https://storybook.js.org/docs/writing-stories
const meta: any = {
  title: 'sequence/SequenceConfiguration',
  component: SequenceConfiguration,
  args: {
    maxResponseToEvaluate: 5,
    aiIsActivated: true,
    questionIsOpen: false,
    onCancelSequenceConfiguration: fn(),
    onSubmitSequenceConfiguration: fn()
  },
  tags: ['autodocs', 'pages'],
  parameters: {
    docs: {
      description: {
        story: 'TODO'
      }
    }
  }
} satisfies Meta<typeof SequenceConfiguration >;

export default meta;
type Story = StoryObj<typeof meta>;


export const Primary: Story = {
};

export const WithoutAI: Story = {
  args: {
    aiIsActivated: false
  }
}

export const QuestionIsOpen: Story = {
  args: {
    questionIsOpen: true
  }
}
