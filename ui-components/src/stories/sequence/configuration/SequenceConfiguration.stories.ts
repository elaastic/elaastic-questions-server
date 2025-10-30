/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import type { Meta, StoryObj } from '@storybook/vue3-vite'
import SequenceConfiguration from '@/components/sequence/configuration/SequenceConfiguration.vue'
import { fn } from 'storybook/test'

// More on how to set up stories at: https://storybook.js.org/docs/writing-stories
const meta: any = {
  title: 'sequence/configuration/SequenceConfiguration',
  component: SequenceConfiguration,
  args: {
    maxResponseToEvaluate: 5,
    aiIsActivated: true,
    questionIsOpen: false,
    onCancelSequenceConfiguration: fn(),
    onSubmitSequenceConfiguration: fn(),
  },
  tags: ['autodocs', 'pages'],
  parameters: {
    docs: {
      description: {
        story: 'TODO',
      },
    },
  },
} satisfies Meta<typeof SequenceConfiguration>

export default meta
type Story = StoryObj<typeof meta>

export const Primary: Story = {}

export const WithoutAI: Story = {
  args: {
    aiIsActivated: false,
  },
}

export const QuestionIsOpen: Story = {
  args: {
    questionIsOpen: true,
  },
}
