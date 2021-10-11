{% for lang in site.languages %}
{% if forloop.index0 == 0 %} [fr]({{ page.url }}){: .btn .float-left} {% else %} [{{ lang }}](/{{ lang }}{{ page.url }}){: .btn } {% endif %}
{% endfor %}