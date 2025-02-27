import type {Meta, StoryObj} from '@storybook/vue3';
import DraxoGrid from "@/components/evaluation/draxo/DraxoGrid.vue";
import {OptionType} from "@/components/evaluation/draxo/OptionType";
import {Option} from "@/components/evaluation/draxo/Option";

function optionTypeControl(): any {
  return {
    options: [Option.YES.cssClass, Option.NO.cssClass, Option.DONT_KNOW.cssClass, null],
    mapping: [Option.YES, Option.NO, Option.DONT_KNOW, null],
    control: {type: 'select'}
  };
}

// More on how to set up stories at: https://storybook.js.org/docs/writing-stories
const meta: any = {
  title: 'evaluation/draxo/DraxoGrid',
  component: DraxoGrid,
  args: {
    criteriaD: null,
    criteriaR: null,
    criteriaA: null,
    criteriaX: null,
    criteriaO: null,
  },
  argTypes: {
    criteriaD: optionTypeControl(),
    criteriaR: optionTypeControl(),
    criteriaA: optionTypeControl(),
    criteriaX: optionTypeControl(),
    criteriaO: optionTypeControl(),
  },
  tags: ['autodocs'],
  parameters: {
    docs: {
      description: {
        story: 'A DRAXO grid.'
      }
    }
  }
} satisfies Meta<typeof DraxoGrid >;

export default meta;
type Story = StoryObj<typeof meta>;


export const NotComplete: Story = {
  parameters: {
    docs: {
      description: {
        story: 'A DRAXO grid, where the grader think the response isn\'t complete'
      }
    }
  },
  args: {
    criteriaD: Option.YES,
    criteriaR: Option.YES,
    criteriaA: Option.YES,
    criteriaX: Option.NO,
  }
};

export const NotUnderstandable: Story = {
  parameters: {
    docs: {
      description: {
        story: 'A DRAXO grid, where the grader think the response isn\'t understandable'
      }
    }
  },
  args: {
    criteriaD: Option.NO
  }
};

export const Perfect: Story = {
  parameters: {
    docs: {
      description: {
        story: 'A DRAXO grid, where the grader doesn\'t see any issue with the response'
      }
    }
  },
  args: {
    criteriaD: Option.YES,
    criteriaR: Option.YES,
    criteriaA: Option.YES,
    criteriaX: Option.YES,
    criteriaO: Option.YES,
  }
};
