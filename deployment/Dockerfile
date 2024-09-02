FROM gradle:8.10.0-jdk22-alpine AS base

WORKDIR /app
COPY development/build.gradle ./
RUN gradle clean # Downloads dependencies for cache layer
COPY development ./
RUN gradle assembleBootDist
RUN cp build/distributions/*.zip /srv/ \
    && cd /srv \
    && unzip *.zip \
    && rm *.zip

FROM amazoncorretto:22.0.2-alpine AS production

COPY --from=base /srv/app* /srv
ENV JAVA_OPTS "--enable-preview"
EXPOSE 8080
CMD /srv/bin/app
