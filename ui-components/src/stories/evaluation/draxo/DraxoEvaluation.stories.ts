import type {Meta, StoryObj} from '@storybook/vue3';
import DraxoEvaluation from "@/components/evaluation/draxo/DraxoEvaluation.vue";
import {Criteria} from "@/components/evaluation/draxo/Criteria";
import {OptionType} from "@/components/evaluation/draxo/OptionType";
import {Option} from "@/components/evaluation/draxo/Option";

// More on how to set up stories at: https://storybook.js.org/docs/writing-stories
const meta: any = {
    title: 'evaluation/draxo/DraxoEvaluation',
    component: DraxoEvaluation,
    args: {
        rejectedCriteria: null,
        rejectedOption: null,
        grader: "John Grader",
        graderComment: "Comment about the rejected criteria",
        score: 0,
        isTeacher: false
    },
    argTypes: {
        score: {control: {type: 'number'}},
    },
    tags: ['autodocs'],
    parameters: {
        docs: {
            description: {
                story: 'TODO'
            }
        }
    }
} satisfies Meta<typeof DraxoEvaluation>;

export default meta;
type Story = StoryObj<typeof meta>;


export const Primary: Story = {
    parameters: {
        docs: {
            description: {
                story: 'TODO'
            }
        }
    },
    args:{
        rejectedCriteria: Criteria.D,
        rejectedOption: Option.NO,
    }
};
