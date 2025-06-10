import type {Meta, StoryObj} from '@storybook/vue3-vite';
import SequenceConfiguration from "@/components/sequence/SequenceConfiguration.vue";


// More on how to set up stories at: https://storybook.js.org/docs/writing-stories
const meta: any = {
  title: 'sequence/SequenceConfiguration',
  component: SequenceConfiguration,
  args: {},
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
  parameters: {
    docs: {
      description: {
        story: 'TODO'
      }
    }
  }
};
