import type {Meta, StoryObj} from '@storybook/vue3-vite';
import Link from "@/components/util/Link.vue";


// More on how to set up stories at: https://storybook.js.org/docs/writing-stories
const meta: any = {
  title: 'util/Link',
  component: Link,
  argTypes:{
    target:{
      control: { type: 'select' },
      options: ['_blank', '_self', '_parent', '_top']
    }
  },
  args: {},
  tags: ['autodocs', 'atomic'],
  parameters: {
    docs: {
      description: {
        story: 'Display a link to clik on.'
      }
    }
  }
} satisfies Meta<typeof Link>;

export default meta;
type Story = StoryObj<typeof meta>;


export const Primary: Story = {
  parameters: {
    docs: {
      description: {
        story: 'A link to example.com with text "Example Link". Clicking it will open the link in a new tab.'
      }
    }
  },
  args: {
    href: 'https://example.com',
    text: 'Example Link',
    target: '_blank',
  }
};
