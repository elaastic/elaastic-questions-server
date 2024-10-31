CKEDITOR.editorConfig = function (config) {
  config.floatSpaceDockedOffsetY = 10
  config.floatSpaceDockedOffsetX = 60
  config.extraPlugins = 'pbckcode,confighelper,mathjax'

  config.pbckcode = {
    modes: [
      ['C/C++', 'c_pp'],
      ['C9Search', 'c9search'],
      ['Clojure', 'clojure'],
      ['CoffeeScript', 'coffee'],
      ['ColdFusion', 'coldfusion'],
      ['C#', 'csharp'],
      ['CSS', 'css'],
      ['Diff', 'diff'],
      ['Glsl', 'glsl'],
      ['Go', 'golang'],
      ['Groovy', 'groovy'],
      ['haXe', 'haxe'],
      ['HTML', 'html'],
      ['Jade', 'jade'],
      ['Java', 'java'],
      ['JavaScript', 'javascript'],
      ['JSON', 'json'],
      ['JSP', 'jsp'],
      ['JSX', 'jsx'],
      ['LaTeX', 'latex'],
      ['LESS', 'less'],
      ['Liquid', 'liquid'],
      ['Lua', 'lua'],
      ['LuaPage', 'luapage'],
      ['Markdown', 'markdown'],
      ['OCaml', 'ocaml'],
      ['Perl', 'perl'],
      ['pgSQL', 'pgsql'],
      ['PHP', 'php'],
      ['Powershell', 'powershel1'],
      ['Python', 'python'],
      ['R', 'ruby'],
      ['OpenSCAD', 'scad'],
      ['Scala', 'scala'],
      ['SCSS/Sass', 'scss'],
      ['SH', 'sh'],
      ['SQL', 'sql'],
      ['SVG', 'svg'],
      ['Tcl', 'tcl'],
      ['Text', 'text'],
      ['Textile', 'textile'],
      ['XML', 'xml'],
      ['XQuery', 'xq'],
      ['YAML', 'yaml']
    ],
  }

  config.toolbarGroups = [
    {name: 'document', groups: ['mode', 'document', 'doctools']},
    {name: 'clipboard', groups: ['clipboard', 'undo']},
    // {
    //   name: 'editing', groups:
    //     ['find', 'selection', 'spellchecker', 'editing']
    // },

    // {
    //   name: 'forms', groups:
    //     ['forms']
    // },

    {
      name: 'basicstyles', groups:
        ['basicstyles', 'cleanup']
    },
    {
      name: 'links', groups:
        ['links']
    },
    {
      name: 'paragraph', groups:
        ['list', 'indent', 'blocks', 'paragraph']
    },
    {
      name: 'insert', groups:
        ['insert']
    },
    // {
    //   name: 'styles', groups:
    //     ['styles']
    // },
    {
      name: 'colors', groups:
        ['colors']
    },
    // {
    //   name: 'tools', groups:
    //     ['tools']
    // },
    // {
    //   name: 'others', groups:
    //     ['others']
    // },
    // {
    //   name: 'about', groups:
    //     ['about']
    // },
    {name: 'pbckcode'}
  ]

  config.removeButtons = 'Scayt,SelectAll,Find,Save,NewPage,Preview,Print,Templates,Cut,Copy,Redo,Paste,PasteText,PasteFromWord,Undo,Form,HiddenField,Radio,TextField,Checkbox,Textarea,Select,Button,ImageButton,CreateDiv,BidiLtr,BidiRtl,Language,Anchor,Flash,PageBreak,Iframe,BGColor,ShowBlocks,Maximize,About,Replace,Source,Strike,Subscript,Superscript'
  config.removePlugins = 'smiley,horizontalrule,elementspath,specialchar'
  config.skin = 'minimalist'
  config.mathJaxClass = 'math-tex';
  config.mathJaxLib = '//cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.4/MathJax.js?config=TeX-AMS_HTML';
  config.versionCheck = false;
}
