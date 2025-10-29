FROM ruby:3.4-alpine

WORKDIR /app

RUN apk add --no-cache build-base openssl git && \
    echo "gem: --no-ri --no-rdoc" > ~/.gemrc && \
    gem install bundler

COPY Gemfile* .
RUN bundle install

EXPOSE 4000
ENTRYPOINT ["bundle", "exec", "jekyll", "serve", "--host", "0.0.0.0"]
