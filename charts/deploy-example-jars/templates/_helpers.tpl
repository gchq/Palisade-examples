# Copyright 2020 Crown Copyright
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

{{/*
Expand the name of the chart.
*/}}
{{- define "c.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "deploy-example-jars.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- $name := default .Chart.Name .Values.nameOverride -}}
{{- if contains $name .Release.Name -}}
{{- .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}
{{- end -}}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "deploy-example-jars.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Common labels
*/}}
{{- define "deploy-example-jars.labels" -}}
app.kubernetes.io/name: {{ include "deploy-example-jars.name" . }}
helm.sh/chart: {{ include "deploy-example-jars.chart" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end -}}

{{/*
Calculate a storage path based on the code release artifact id or the supplied value of codeRelease
*/}}
{{- define "deploy-example-jars.deployment.path" }}
{{- if eq .Values.global.deployment "codeRelease" }}
{{- $path := .Values.image.codeRelease | lower | replace "." "-" | trunc 63 | trimSuffix "-" }}
{{- printf "%s/%s/classpath/%s" .Values.global.persistence.classpathJars.mountPath .Chart.Name $path }}
{{- else }}
{{- $path := .Values.global.deployment | lower | replace "." "-" | trunc 63 | trimSuffix "-" }}
{{- printf "%s/%s/classpath/%s" .Values.global.persistence.classpathJars.mountPath .Chart.Name $path }}
{{- end }}
{{- end }}

{{/*
Calculate a storage name based on the code release artifact id or the supplied value of codeRelease
*/}}
{{- define "deploy-example-jars.deployment.name" }}
{{- include "deploy-example-jars.deployment.path" . | base }}
{{- end }}

{{/*
Calculate a storage full name based on the code release artifact id or the supplied value of codeRelease
*/}}
{{- define "deploy-example-jars.deployment.fullname" }}
{{- .Values.global.persistence.classpathJars.name }}-{{- include "deploy-example-jars.deployment.name" . }}
{{- end }}