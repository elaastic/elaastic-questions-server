import type { Meta, StoryObj } from '@storybook/vue3'
import ElContentBlock from '@/components/player/ElContentBlock.vue'
import ElHtmlContent from '@/components/player/ElHtmlContent.vue'
import exampleImage from '@/stories/assets/statement/Survivorship-bias.png'

const meta = {
  title: 'player/ElContentBlock',
  component: ElContentBlock,
  tags: ['autodocs']
} satisfies Meta<typeof ElContentBlock>

export default meta
type Story = StoryObj<typeof meta>

export const Default: Story = {
  render: (args) => ({
    components: { ElContentBlock },
    setup() {
      return {
        args
      }
    },
    template: `
      <ElContentBlock v-bind="args" v-model:open="args.open">
        <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam eu nunc nisl. In tincidunt, ante et venenatis aliquam, leo nulla interdum mauris, at volutpat magna dui nec felis. Nam ac vestibulum nibh. Vivamus vitae risus neque. Duis ultrices dui ut.</p>
      </ElContentBlock>
    `
  }),
  args: {
    title: 'Mastering the Art of Productivity: Tips and Tricks for a More Efficient Life',
    open: true
  }
}

export const NotCollapsible: Story = {
  render: (args) => ({
    components: { ElContentBlock },
    setup() {
      return {
        args
      }
    },
    template: `
      <ElContentBlock v-bind="args">
        <p>I'm a block of content that can't be folded.</p>
      </ElContentBlock>
    `
  }),
  args: {
    title: 'Not collapsible',
    collapsible: false
  }
}
export const Closed: Story = {
  render: (args) => ({
    components: { ElContentBlock },
    setup() {
      return {
        args
      }
    },
    template: `
      <ElContentBlock v-bind="args" v-model:open="args.open">
        <p>I'm a closed content block.</p>
      </ElContentBlock>
    `
  }),
  args: {
    title: 'Closed content block',
    open: false
  }
}

export const WithSubtitle: Story = {
  render: (args) => ({
    components: { ElContentBlock },
    setup() {
      return {
        args
      }
    },
    template: `
      <ElContentBlock
              v-bind="args"
              v-model:open="args.open"
      >
        <p>This content block illustrates a title with a subtitle.</p>
      </ElContentBlock>
    `
  }),
  args: {
    title: 'I have a title',
    collapsible: true,
    open: true,
    subtitle: 'and a subtitle'
  }
}

export const Results: Story = {
  render: (args) => ({
    components: { ElContentBlock },
    setup() {
      return { args }
    },
    template: `
      <ElContentBlock v-bind="args" v-model:open="args.open">
        <div>
          <h3>Choix</h3>
          <div style="display: flex">
            <v-card style="width: 50px; background-color: green">
              <v-card-text style="color: white">1</v-card-text>
            </v-card>
            <v-card style="width: 50px; background-color: red">
              <v-card-text style="color: white">2</v-card-text>
            </v-card>
          </div>
          <p>Score</p>
          <v-card style="width: 60px; background-color: blue">
            <v-card-text style="color: white">50%</v-card-text>
          </v-card>
        </div>
      </ElContentBlock>
    `
  }),
  args: {
    title: 'Results',
    collapsible: false,
    open: true
  }
}

export const Statement: Story = {
  render: (args) => ({
    components: { ElContentBlock, ElHtmlContent },
    setup() {
      return {
        args,
        exampleImage
      }
    },
    template: `
      <ElContentBlock v-bind="args" v-model:open="args.open">
        <ElHtmlContent>
          <div class="transition visible" style="display: block !important;">

            <div xmlns="http://www.w3.org/1999/html">
              <div>
                <img height="380" :src="exampleImage" width="510" alt="survivor biais">
              </div>
            </div>

            <p></p>
            <p>Pendant la seconde guerre mondiale, une étude sur les avions de retours de bataille a permis d'identifier
              les zones de la carlingue les plus touchées par les impacts de balles. Les ingénieurs ont&nbsp;utilisé ces
              informations pour effectuer des renforcements de la carlingue des avions. D'après vous, quelles zones ont
              été renforcées :</p>

            <ol>
              <li>Les zones les plus touchées par les impacts de balle</li>
              <li>Les zones les moins touchées par les impacts de balle</li>
              <li>Aucune zone car l'information ne permettait pas de conclure sur les zones à renforcer</li>
            </ol>

            <p>&nbsp;</p>

            <p><strong>Justifiez</strong>&nbsp;votre choix dans le champ&nbsp;"<em>réponse textuelle</em>" !</p>
            <p></p>
          </div>
        </ElHtmlContent>
      </ElContentBlock>
    `
  }),
  args: {
    title: 'Renforcement de la carlingue des avions de la 2nde guerre mondiale',
    subtitle: 'Question à choix exclusif',
    open: true
  }
}
