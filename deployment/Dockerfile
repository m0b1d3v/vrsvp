FROM gradle:9.1.0-jdk25-alpine AS base

WORKDIR /app
COPY development/build.gradle ./
RUN gradle clean # Downloads dependencies for cache layer
COPY development ./
RUN gradle assembleBootDist
RUN cp build/distributions/*.zip /srv/ \
    && cd /srv \
    && unzip *.zip \
    && rm *.zip

FROM amazoncorretto:25.0.1-alpine AS production

COPY --from=base /srv/app* /srv
EXPOSE 8080
CMD /srv/bin/app
