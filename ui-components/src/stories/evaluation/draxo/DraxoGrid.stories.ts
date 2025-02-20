import type {Meta, StoryObj} from '@storybook/vue3';
import DraxoGrid from "@/components/evaluation/draxo/DraxoGrid.vue";
import {OptionType} from "@/components/evaluation/draxo/OptionType";

const getOptionsType = () => {
  return [null, ...Object.values(OptionType)];
}
function getCriteriaControl(): any {
  return {options: getOptionsType(), control: {type: 'select'}};
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
    criteriaD: getCriteriaControl(),
    criteriaR: getCriteriaControl(),
    criteriaA: getCriteriaControl(),
    criteriaX: getCriteriaControl(),
    criteriaO: getCriteriaControl(),
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
    criteriaD: OptionType.YES,
    criteriaR: OptionType.YES,
    criteriaA: OptionType.YES,
    criteriaX: OptionType.NO,
    criteriaO: null,
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
    criteriaD: OptionType.NO
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
    criteriaD: OptionType.YES,
    criteriaR: OptionType.YES,
    criteriaA: OptionType.YES,
    criteriaX: OptionType.YES,
    criteriaO: OptionType.YES,
  }
};
